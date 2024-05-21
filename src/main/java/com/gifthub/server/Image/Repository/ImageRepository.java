package com.gifthub.server.Image.Repository;

import com.gifthub.server.Image.Entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {

    List<ImageEntity> findByRoomIdAndCategoryId(Long roomId, Long categoryId);

    ImageEntity findByUrl(String url);

    boolean existsByBarcode(String barcode);

    void deleteAllByUserId(Long userId);
    void deleteAllByRoomId(Long roomId);
}
