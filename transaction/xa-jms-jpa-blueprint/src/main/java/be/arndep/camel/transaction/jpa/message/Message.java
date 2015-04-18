package be.arndep.camel.transaction.jpa.message;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by arnaud on 03.01.15.
 */
@Entity
@Data
public class Message implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    private Date createdDate;
    private Date lastModifiedDate;
    private String content;

    public Message() {
    }
    
    @PrePersist
    public void prePersist() {
        createdDate = new Date();
        lastModifiedDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = new Date();
    }
}
