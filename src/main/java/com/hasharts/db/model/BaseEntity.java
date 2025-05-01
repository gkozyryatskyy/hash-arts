package com.hasharts.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class BaseEntity extends IdEntity {

    @EqualsAndHashCode.Exclude
    @Column(updatable = false)
    private Instant createdAt;
    @EqualsAndHashCode.Exclude
    private Instant updatedAt;

    @SuppressWarnings("unused")
    public BaseEntity(BaseEntity other) {
        super(other.getId());
        this.createdAt = other.getCreatedAt();
        this.updatedAt = other.getUpdatedAt();
    }

}
