package br.edu.utfpr.cp.cloudtesterweb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "storage",
        indexes = {
            @Index(columnList = "dateTime", name = "idx_storage_dateTime"),
            @Index(columnList = "testTimesConfig", name = "idx_storage_testTimesConfig"),
            @Index(columnList = "completed", name = "idx_storage_completed")
        })
@NamedQueries({
    @NamedQuery(name = StorageEntity.FIND_TO_EXECUTE,
            query = "SELECT f FROM StorageEntity f WHERE f.completed = false ORDER BY f.dateTime DESC"),
    @NamedQuery(name = StorageEntity.FIND_ALL,
            query = "SELECT f FROM StorageEntity f ORDER BY f.dateTime DESC")
})
public class StorageEntity implements Serializable {

    public static final String FIND_TO_EXECUTE = "StorageEntity.findToExecute";
    public static final String FIND_ALL = "StorageEntity.findAll";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "dateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false)
    private Long contentLength;
    @Column(nullable = false)
    private String contentPath;
    @Column(nullable = false, length = 100)
    private String contentType;
    @Column(nullable = false, name = "testTimesConfig")
    private Integer configTestTimes;

    @Column(name = "name", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = PlatformType.class)
    @CollectionTable(name = "storage_platform",
            joinColumns = @JoinColumn(name = "storage_id", referencedColumnName = "id"),
            indexes = @Index(columnList = "name", name = "idx_storage_platform_name"))
    private List<PlatformType> configTestPlatforms;

    @Column(name = "name", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ApiType.class)
    @CollectionTable(name = "storage_api",
            joinColumns = @JoinColumn(name = "storage_id", referencedColumnName = "id"),
            indexes = @Index(columnList = "name", name = "idx_storage_api_name"))
    private List<ApiType> configTestApis;

    @Column(name = "name", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = FeatureType.class)
    @CollectionTable(name = "storage_feature",
            joinColumns = @JoinColumn(name = "storage_id", referencedColumnName = "id"),
            indexes = @Index(columnList = "name", name = "idx_storage_feature_name"))
    private List<FeatureType> configTestFeatures;

    @Column(nullable = false, name = "completed")
    private Boolean completed = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "storage_test",
            joinColumns = {
                @JoinColumn(name = "storage_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "test_id", referencedColumnName = "id")},
            indexes = @Index(columnList = "storage_id,test_id", name = "idx_storage_test_storage_id_test_id"))
    private List<TestEntity> tests;

    public StorageEntity() {
        tests = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public Double getContentLengthKB() {
        return contentLength / 1024d;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getConfigTestTimes() {
        return configTestTimes;
    }

    public void setConfigTestTimes(Integer configTestTimes) {
        this.configTestTimes = configTestTimes;
    }

    public List<PlatformType> getConfigTestPlatforms() {
        return configTestPlatforms;
    }

    public void setConfigTestPlatforms(List<PlatformType> configTestPlatforms) {
        this.configTestPlatforms = configTestPlatforms;
    }

    public List<ApiType> getConfigTestApis() {
        return configTestApis;
    }

    public void setConfigTestApis(List<ApiType> configTestApis) {
        this.configTestApis = configTestApis;
    }

    public List<FeatureType> getConfigTestFeatures() {
        return configTestFeatures;
    }

    public void setConfigTestFeatures(List<FeatureType> configTestFeatures) {
        this.configTestFeatures = configTestFeatures;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<TestEntity> getTests() {
        return tests;
    }

    public void setTests(List<TestEntity> tests) {
        this.tests = tests;
    }

    public void addTest(TestEntity test) {
        if (!tests.contains(test)) {
            tests.add(test);
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof StorageEntity)) {
            return false;
        }
        StorageEntity other = (StorageEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
