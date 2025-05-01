package com.hasharts.db.model.nft;

import com.hasharts.db.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Token extends BaseEntity {

    private String hederaTokenId;
    private String name;
    private String symbol;
    private String supplyPublicKey;
    private String supplyPrivateKey;
}
