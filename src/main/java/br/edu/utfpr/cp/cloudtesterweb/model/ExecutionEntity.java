package br.edu.utfpr.cp.cloudtesterweb.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "execution")
public class ExecutionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionApi api;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionType type;
    @Column(nullable = false)
    private Long durationTime;
    @Column(nullable = false)
    private Boolean success;
    @Lob
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecutionApi getApi() {
        return api;
    }

    public void setApi(ExecutionApi api) {
        this.api = api;
    }

    public ExecutionType getType() {
        return type;
    }

    public void setType(ExecutionType type) {
        this.type = type;
    }

    public Long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Long durationTime) {
        this.durationTime = durationTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
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
        if (!(object instanceof ExecutionEntity)) {
            return false;
        }
        ExecutionEntity other = (ExecutionEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
