package br.edu.utfpr.cp.cloudtesterweb.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Douglas
 */
@Entity
@Table(name = "errorMessage")
@NamedQueries({
    @NamedQuery(name = ErrorMessage.DELETE_BY_FILE,
            query = "DELETE FROM ErrorMessage e WHERE EXISTS (SELECT t FROM TestExecutionEntity t WHERE t.test.file = :file AND t.errorMessageId = e.id)")
})
public class ErrorMessage implements Serializable {

    public static final String DELETE_BY_FILE = "ErrorMessage.deleteByFile";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String message;

    public ErrorMessage() {
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        if (!(object instanceof ErrorMessage)) {
            return false;
        }
        ErrorMessage other = (ErrorMessage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ErrorMessage[ id=" + id + " ]";
    }

}
