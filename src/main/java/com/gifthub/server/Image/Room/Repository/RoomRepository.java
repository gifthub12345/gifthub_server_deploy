package com.gifthub.server.Image.Room.Repository;

import com.gifthub.server.Image.Room.Entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    RoomEntity findByCode(String code);

}
