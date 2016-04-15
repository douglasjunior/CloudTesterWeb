package br.edu.utfpr.cp.cloudtesterweb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "test",
        indexes = {
            @Index(columnList = "file_id")
        }
)
@NamedQueries({})
public class TestEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "file_id", nullable = false)
    @ManyToOne(optional = false)
    private FileEntity file;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApiType api;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureType feature;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlatformType platform;
    @Column(nullable = false, length = 255)
    private String containerName;

    @OneToMany(mappedBy = "test", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TestExecutionEntity> executions;

    public TestEntity(FileEntity file, PlatformType platform, ApiType api, FeatureType feature, String containerName) {
        this();
        this.file = file;
        this.api = api;
        this.feature = feature;
        this.platform = platform;
        this.containerName = containerName;
    }

    public TestEntity() {
        executions = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public ApiType getApi() {
        return api;
    }

    public void setApi(ApiType api) {
        this.api = api;
    }

    public FeatureType getFeature() {
        return feature;
    }

    public void setFeature(FeatureType feature) {
        this.feature = feature;
    }

    public PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformType platform) {
        this.platform = platform;
    }

    public List<TestExecutionEntity> getExecutions() {
        return executions;
    }

    public void setExecutions(List<TestExecutionEntity> executions) {
        this.executions = executions;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TestEntity)) {
            return false;
        }
        TestEntity other = (TestEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
