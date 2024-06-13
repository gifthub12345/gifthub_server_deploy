package com.gifthub.server.Image.Room.Controller;

import com.gifthub.server.Category.Service.CategoryService;
import com.gifthub.server.Image.Room.DTO.RoomJoinDTO;
import com.gifthub.server.Image.Room.DTO.RoomResponseDTO;
import com.gifthub.server.Image.Room.Service.RoomService;
import com.gifthub.server.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final UserService userService;
    private final RoomService roomService;
    private final CategoryService categoryService;


    @PostMapping("/room/enter")
    public ResponseEntity<?> enterRoom(@RequestBody RoomJoinDTO roomJoinDTO,
                                       HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = userService.getUserId(token);
        Long roomId = roomService.enterRoom(userId, roomJoinDTO);

        return new ResponseEntity<>(roomId, HttpStatus.OK);

    }

    @PostMapping("/room/create")
    public ResponseEntity<?> createRoom(HttpServletRequest request, @RequestBody RoomJoinDTO roomJoinDTO) {
        String token = request.getHeader("Authorization");
        Long userId = userService.getUserId(token);
        RoomResponseDTO responseDTO = roomService.createRoom(userId, roomJoinDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/room/main/{room_id}")
    public ResponseEntity<?> mainRoom(@PathVariable("room_id") Long room_id) {
        // List<CategoryDTO> categoryDTOList = categoryService.CategoryDTOList(categoryService.getAllCategories());
        String roomTitle = roomService.getRoomTitle(room_id);
        return new ResponseEntity<>(roomTitle, HttpStatus.OK);
    }

    @PostMapping("/room/exit/{room_id}")
    public ResponseEntity<?> exitRoom(@PathVariable("room_id") Long roomId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = userService.getUserId(token);
        roomService.exitRoom(userId, roomId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/room/{room_id}/users")
    public ResponseEntity<?> getUsers(@PathVariable("room_id") Long room_id) throws IOException {
        List<String> userEmailList = userService.getUserList(room_id);
        return new ResponseEntity<>(userEmailList, HttpStatus.OK);
    }

    @GetMapping("/room/{room_id}/share")
    public ResponseEntity<?> shareRoom(@PathVariable("room_id")Long room_id){
        return new ResponseEntity<>(roomService.shareRoomCode(room_id), HttpStatus.OK);
    }
}
