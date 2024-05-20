package com.gifthub.server.Image.Entity;

import com.gifthub.server.Category.Entity.CategoryEntity;
import com.gifthub.server.Room.Entity.RoomEntity;
import com.gifthub.server.User.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor
@Table(name= "gifticon")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gifticon_id")
    private Long id;

    private String url;
    private LocalDate expire;
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @Builder
    public ImageEntity(Long id, String url, LocalDate expire, String barcode, CategoryEntity category, UserEntity user, RoomEntity room) {
        this.id = id;
        this.url = url;
        this.expire = expire;
        this.barcode = barcode;
        this.category = category;
        this.user = user;
        this.room = room;
    }
}
