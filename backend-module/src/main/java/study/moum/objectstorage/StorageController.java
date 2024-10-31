package study.moum.objectstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    /**
     * 파일 업로드 API
     */
    @PostMapping("/storage/upload")
    public String uploadFile(@RequestPart("file") MultipartFile multipartFile) throws Exception {
        String filename = multipartFile.getOriginalFilename();
        storageService.uploadFile(filename, multipartFile);
        return "File uploaded successfully!";
    }

    /**
     * 파일 다운로드 API
     */
    @GetMapping("/storage/download/{filename}")
    public byte[] downloadFile(@PathVariable String filename) throws Exception {
        return storageService.downloadFile(filename);
    }

    /**
     * 파일 삭제 API
     */
    @DeleteMapping("/storage/delete/{filename}")
    public String deleteFile(@PathVariable String filename) {
        storageService.deleteFile(filename);
        return "File deleted successfully!";
    }
}
