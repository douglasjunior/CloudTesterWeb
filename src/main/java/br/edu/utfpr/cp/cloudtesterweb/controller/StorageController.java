package br.edu.utfpr.cp.cloudtesterweb.controller;

import br.edu.utfpr.cp.cloudtesterweb.csv.StorageHeaders;
import br.edu.utfpr.cp.cloudtesterweb.dao.DaoStatefull;
import br.edu.utfpr.cp.cloudtesterweb.model.ApiType;
import br.edu.utfpr.cp.cloudtesterweb.model.CloudConfiguration;
import br.edu.utfpr.cp.cloudtesterweb.model.ErrorMessageEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.FeatureType;
import br.edu.utfpr.cp.cloudtesterweb.model.StorageEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.PlatformType;
import br.edu.utfpr.cp.cloudtesterweb.model.TestEntity;
import br.edu.utfpr.cp.cloudtesterweb.model.TestExecutionEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.transaction.UserTransaction;
import static br.edu.utfpr.cp.cloudtesterweb.util.Constants.*;
import br.edu.utfpr.cp.cloudtesterweb.util.EnumConverter;
import br.edu.utfpr.cp.cloudtesterweb.util.ValidationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import javax.servlet.http.Part;

/**
 *
 * @author Douglas
 */
@Named
@ViewScoped
public class StorageController implements Serializable {

    public static EnumConverter<ApiType> API_CONVERTER = new EnumConverter<>(ApiType.values());
    public static EnumConverter<PlatformType> PLATFORM_CONVERTER = new EnumConverter<>(PlatformType.values());
    public static EnumConverter<FeatureType> FEATURE_CONVERTER = new EnumConverter<>(FeatureType.values());

    private static final FeatureType[] FEATURES = {FeatureType.STORAGE_UPLOAD, FeatureType.STORAGE_DOWNLOAD, FeatureType.STORAGE_LIST, FeatureType.STORAGE_DELETE};
    private static final ApiType[] APIS = {ApiType.AWS_NATIVE, ApiType.AZURE_NATIVE, ApiType.JCLOUDS};
    private static final PlatformType[] PLATFORMS = {PlatformType.AWS, PlatformType.AZURE};

    @EJB
    private DaoStatefull dao;

    @EJB
    private CloudController cloudController;

    @Resource
    private UserTransaction tx;

    private List<StorageEntity> storages;
    private String errorMessage = "";
    private Part uploadedFile;
    private int times = 1;

    private List<ApiType> selectedApis = new ArrayList<>();
    private List<PlatformType> selectedPlatforms = new ArrayList<>();
    private List<FeatureType> selectedFeatures = new ArrayList<>();

    private final Set<CloudConfiguration> configurations = new LinkedHashSet<>();

    public StorageController() {
    }

    public void upload() {
        try {
            validate();

            File fileOut = copyFile();
            tx.begin();
            StorageEntity entity = new StorageEntity();
            entity.setDateTime(new Date());
            entity.setFileName(uploadedFile.getSubmittedFileName());
            entity.setFileContentLength(uploadedFile.getSize());
            entity.setFilePath(fileOut.getAbsolutePath());
            entity.setFileContentType(uploadedFile.getContentType());
            entity.setConfigTestTimes(times);
            entity.setConfigTestApis(selectedApis);
            entity.setConfigTestPlatforms(selectedPlatforms);
            entity.setConfigTestFeatures(selectedFeatures);
            dao.insert(entity);
            tx.commit();

            storages.add(entity);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", uploadedFile.getSubmittedFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (ValidationException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                tx.rollback();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            String name = uploadedFile != null ? uploadedFile.getSubmittedFileName() : "File";
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", name + " is not uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    private void validate() throws ValidationException {
        if (selectedApis.isEmpty()) {
            throw new ValidationException("No API selected by filter");
        }
        if (selectedPlatforms.isEmpty()) {
            throw new ValidationException("No Platform selected by filter");
        }
        if (selectedFeatures.isEmpty()) {
            throw new ValidationException("No Feature selected by filter");
        }
        if (configurations.isEmpty()) {
            throw new ValidationException("No Configuration selected by filter");
        }
    }

    public void delete(StorageEntity storage) {
        System.out.println("Delete: " + storage);
        try {
            tx.begin();
            dao.createNamedQuery(ErrorMessageEntity.DELETE_BY_FILE, new String[]{"sotrageId"}, new Object[]{storage.getId()}).executeUpdate();
            dao.delete(storage);
            tx.commit();
            storages.remove(storage);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Succesful", storage.getFileName() + " deleted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                tx.rollback();
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", storage.getFileName() + " is not deleted.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void updateConfigurations() {
        System.out.println("Apis: " + selectedApis);
        System.out.println("Platforms: " + selectedPlatforms);
        System.out.println("Features: " + selectedFeatures);
        configurations.clear();
        for (ApiType api : selectedApis) {
            for (PlatformType platform : selectedPlatforms) {
                for (FeatureType feature : selectedFeatures) {
                    CloudConfiguration config = cloudController.getConfiguration(
                            (platform),
                            (api),
                            (feature));
                    if (config != null) {
                        configurations.add(config);
                    }
                }
            }
        }

    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public List<StorageEntity> getFiles() {
        if (storages == null) {
            refresh();
        }
        return storages;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Throwable.class})
    public void refresh() {
        storages = dao.createNamedQuery(StorageEntity.FIND_ALL, StorageEntity.class).getResultList();
        dao.refreshAll(storages);
    }

    public void viewError(TestExecutionEntity exec) {
        ErrorMessageEntity error = dao.findById(ErrorMessageEntity.class, exec.getErrorMessageId());
        if (error != null) {
            errorMessage = error.getMessage();
        } else {
            errorMessage = "";
        }
    }

    public StreamedContent downloadCSV(StorageEntity file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(baos);
                CSVPrinter printer = CSVFormat.DEFAULT.withHeader(StorageHeaders.class).print(osw)) {

            for (TestEntity test : file.getTests()) {
                for (TestExecutionEntity exec : test.getExecutions()) {
                    printer.print(test.getPlatform());
                    printer.print(test.getApi());
                    printer.print(test.getFeature());
                    printer.print(exec.getDuration());
                    printer.print(exec.getSuccess());
                    printer.println();
                }
            }
            printer.flush();

            InputStream stream = new ByteArrayInputStream(baos.toByteArray());
            StreamedContent content = new DefaultStreamedContent(stream, "text/csv", file.getFileName() + ".csv");
            return content;
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Download failed.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return null;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private File copyFile() throws IOException {
        File directory = new File(UPLOADED_FOLDER);
        directory.mkdirs();
        File fileOut = new File(directory, uploadedFile.getSubmittedFileName());
        try (FileOutputStream fos = new FileOutputStream(fileOut);
                InputStream is = uploadedFile.getInputStream();) {
            IOUtils.copy(is, fos);
        }
        return fileOut;
    }

    public FeatureType[] getFeatures() {
        return FEATURES;
    }

    public ApiType[] getApis() {
        return APIS;
    }

    public PlatformType[] getPlatforms() {
        return PLATFORMS;
    }

    public void setSelectedFeatures(List<FeatureType> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }

    public List<FeatureType> getSelectedFeatures() {
        return selectedFeatures;
    }

    public List<ApiType> getSelectedApis() {
        return selectedApis;
    }

    public void setSelectedApis(List<ApiType> selectedApis) {
        this.selectedApis = selectedApis;
    }

    public List<PlatformType> getSelectedPlatforms() {
        return selectedPlatforms;
    }

    public void setSelectedPlatforms(List<PlatformType> selectedPlatforms) {
        this.selectedPlatforms = selectedPlatforms;
    }

    public Set<CloudConfiguration> getConfigurations() {
        return configurations;
    }

    public EnumConverter<ApiType> getApiConverter() {
        return API_CONVERTER;
    }

    public EnumConverter<PlatformType> getPlatformConverter() {
        return PLATFORM_CONVERTER;
    }

    public EnumConverter<FeatureType> getFeatureConverter() {
        return FEATURE_CONVERTER;
    }

}
