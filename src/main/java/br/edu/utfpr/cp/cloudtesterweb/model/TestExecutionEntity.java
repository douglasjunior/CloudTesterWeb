package br.edu.utfpr.cp.cloudtesterweb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "test_execution",
        indexes = {
            @Index(columnList = "test_id", name = "idx_test_execution_test_id"),
            @Index(columnList = "dateTimeStart", name = "idx_test_execution_dateTimeStart"),
            @Index(columnList = "dateTimeEnd", name = "idx_test_execution_dateTimeEnd")
        }
)
public class TestExecutionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "test_id", nullable = false)
    @ManyToOne(optional = false)
    private TestEntity test;

    @Column(name = "dateTimeStart")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeStart;
    @Column(name = "dateTimeEnd")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeEnd;
    @Column(nullable = false)
    private Boolean success;
    private Long errorMessageId;
    @Transient
    private ErrorMessageEntity errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TestEntity getTest() {
        return test;
    }

    public void setTest(TestEntity test) {
        this.test = test;
    }

    public Date getDateTimeStart() {
        return dateTimeStart;
    }

    public void setDateTimeStart(Date dateTimeStart) {
        this.dateTimeStart = dateTimeStart;
    }

    public Date getDateTimeEnd() {
        return dateTimeEnd;
    }

    public void setDateTimeEnd(Date dateTimeEnd) {
        this.dateTimeEnd = dateTimeEnd;
    }

    public Long getDuration() {
        return dateTimeStart == null || dateTimeEnd == null ? 0
                : dateTimeEnd.getTime() - dateTimeStart.getTime();
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getErrorMessageId() {
        return errorMessageId;
    }

    public void setErrorMessageId(Long errorMessageId) {
        this.errorMessageId = errorMessageId;
    }

    public ErrorMessageEntity getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessageEntity errorMessage) {
        this.errorMessage = errorMessage;
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
        if (!(object instanceof TestExecutionEntity)) {
            return false;
        }
        TestExecutionEntity other = (TestExecutionEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
