package com.senpro.jafrabackend.models.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "wishlist")
public class WishListEntry {
    @Id
    private WishListEntry.WishListKey id;
    private Date listAddDate;
    @Data
    static public class WishListKey implements Serializable {
        private String username;
        private String restaurantId;
    }
}
