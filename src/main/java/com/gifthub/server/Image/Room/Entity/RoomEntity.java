package com.gifthub.server.Image.Room.Entity;

import com.gifthub.server.User.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor
@Table(name= "room")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private String code;
    private String title;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<UserEntity> users = new ArrayList<>();

    @Builder
    public RoomEntity(String code, String title) {
        this.code = code;
        this.title = title;
    }
}
