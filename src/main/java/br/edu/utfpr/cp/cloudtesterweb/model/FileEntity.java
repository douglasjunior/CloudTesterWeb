package br.edu.utfpr.cp.cloudtesterweb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
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
@Table(name = "file",
        indexes = {
            @Index(columnList = "dateTime"),
            @Index(columnList = "testTimesConfig"),
            @Index(columnList = "completed")
        })
@NamedQueries({
    @NamedQuery(name = FileEntity.FIND_TO_EXECUTE,
            query = "SELECT f FROM FileEntity f WHERE f.completed = false ORDER BY f.dateTime DESC")
})
public class FileEntity implements Serializable {

    public static final String FIND_TO_EXECUTE = "FileEntity.findToExecute";

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
    private Integer testTimesConfig;
    @Column(nullable = false, name = "completed")
    private Boolean completed = false;
    @OneToMany(mappedBy = "file", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<TestEntity> tests;

    public FileEntity() {
        tests = new LinkedHashSet<>();
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

    public Integer getTestTimesConfig() {
        return testTimesConfig;
    }

    public void setTestTimesConfig(Integer testTimesConfig) {
        this.testTimesConfig = testTimesConfig;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Set<TestEntity> getTests() {
        return tests;
    }

    public void setTests(Set<TestEntity> tests) {
        this.tests = tests;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FileEntity)) {
            return false;
        }
        FileEntity other = (FileEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
