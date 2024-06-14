package com.gifthub.server.Room.DTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomMainDTO {
    String title;

    @Builder
    public RoomMainDTO(String title) {
        this.title = title;
    }
}
