package com.gifthub.server.Room.Entity;

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

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<UserEntity> users = new ArrayList<>();

    @Builder
    public RoomEntity(String code) {
        this.code = code;
    }
}
