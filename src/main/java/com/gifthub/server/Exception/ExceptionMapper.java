package com.gifthub.server.Exception;

import com.gifthub.server.Image.Exception.*;
import com.gifthub.server.Image.Room.Exception.RoomNotFoundException;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExceptionMapper {
    private static final Map<Class<? extends Exception>, ExceptionSituation> mapper = new LinkedHashMap<Class<? extends Exception>, ExceptionSituation>();

    static {
        setUpImageException();
        setUpUserException();
        setUpRoomException();
    }

    private static void setUpImageException() {
        mapper.put(ExtensionUnsuitableException.class,
                ExceptionSituation.of("이미지 파일 형식(확장자)이 잘못되었습니다", HttpStatus.UNSUPPORTED_MEDIA_TYPE, 1201));
        mapper.put(ExtensionNotFoundException.class,
                ExceptionSituation.of("이미지 파일의 확장자를 찾을 수 없습니다", HttpStatus.BAD_REQUEST, 1202));
        mapper.put(ImageEmptyException.class,
                ExceptionSituation.of("비어있는 이미지 파일입니다", HttpStatus.NO_CONTENT, 1203));
        mapper.put(ImageAlreadyExistException.class,
                ExceptionSituation.of("해당 기프티콘은 이미 등록된 기프티콘입니다", HttpStatus.CONFLICT, 1204));
        mapper.put(S3ImageUploadException.class,
                ExceptionSituation.of("s3에 업로드 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR, 1205));
        mapper.put(S3UrlException.class,
                ExceptionSituation.of("s3 버킷 저장 경로가 잘못되었습니다. ", HttpStatus.NOT_FOUND, 1206));
        mapper.put(S3ImageNotFoundException.class,
                ExceptionSituation.of("s3 버킷에서 이미지를 찾을 수 없습니다. ", HttpStatus.NOT_FOUND, 1207));

    }
    private static void setUpUserException() {
    }
    private static void setUpRoomException() {
        mapper.put(RoomNotFoundException.class,
                ExceptionSituation.of("공유 방을 찾을 수 없습니다", HttpStatus.NOT_FOUND, 1401));
    }

    // 해당 예외 클래스에 대한 ExceptionSituation 반환
    public static ExceptionSituation getSituationOf(Exception exception) {
        return mapper.get(exception.getClass());
    }

    // 모든 예외 상황을 담는 리스트 반환
    public static List<ExceptionSituation> getExceptionSituations() {
        return mapper.values()
                .stream()
                .toList();
    }
}
