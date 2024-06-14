package com.gifthub.server.Room.Repository;

import com.gifthub.server.Room.Entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    RoomEntity findByCode(String code);

}
