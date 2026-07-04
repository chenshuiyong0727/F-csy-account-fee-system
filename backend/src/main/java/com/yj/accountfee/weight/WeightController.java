package com.yj.accountfee.weight;

import com.yj.accountfee.common.ApiResult;
import com.yj.accountfee.common.BizException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/weight")
public class WeightController {
    private static final long MAX_AVATAR_SIZE = 8L * 1024 * 1024;
    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
        "image/jpeg", ".jpg",
        "image/png", ".png",
        "image/webp", ".webp"
    );

    private final WeightService weightService;

    @Value("${account-fee.weight.upload-dir:./uploads/weight}")
    private String uploadDir;

    public WeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping("/profiles")
    public ApiResult<List<WeightProfile>> profiles() {
        return ApiResult.success(weightService.profiles());
    }

    @GetMapping("/profiles/{id}")
    public ApiResult<WeightProfile> profile(@PathVariable Long id) {
        return ApiResult.success(weightService.profile(id));
    }

    @PostMapping("/profiles")
    public ApiResult<Long> createProfile(@Valid @RequestBody WeightProfileSaveDTO dto) {
        return ApiResult.success(weightService.createProfile(dto));
    }

    @PutMapping("/profiles/{id}")
    public ApiResult<Void> updateProfile(@PathVariable Long id, @Valid @RequestBody WeightProfileSaveDTO dto) {
        weightService.updateProfile(id, dto);
        return ApiResult.success();
    }

    @PutMapping("/profiles/{id}/avatar")
    public ApiResult<Void> updateAvatar(
        @PathVariable Long id,
        @Valid @RequestBody WeightAvatarUpdateDTO dto
    ) {
        weightService.updateAvatar(id, dto.getAvatarUrl());
        return ApiResult.success();
    }

    @DeleteMapping("/profiles/{id}")
    public ApiResult<Void> deleteProfile(@PathVariable Long id) {
        weightService.deleteProfile(id);
        return ApiResult.success();
    }

    @GetMapping("/profiles/{profileId}/records")
    public ApiResult<List<WeightRecord>> records(@PathVariable Long profileId) {
        return ApiResult.success(weightService.records(profileId));
    }

    @PostMapping("/profiles/{profileId}/records")
    public ApiResult<Long> createRecord(
        @PathVariable Long profileId,
        @Valid @RequestBody WeightRecordSaveDTO dto
    ) {
        return ApiResult.success(weightService.createRecord(profileId, dto));
    }

    @PostMapping("/profiles/{profileId}/records/batch")
    public ApiResult<WeightBatchResultVO> batchCreateRecords(
        @PathVariable Long profileId,
        @Valid @RequestBody WeightBatchSaveDTO dto
    ) {
        return ApiResult.success(weightService.batchCreateRecords(profileId, dto));
    }

    @PutMapping("/records/{id}")
    public ApiResult<Void> updateRecord(
        @PathVariable Long id,
        @Valid @RequestBody WeightRecordSaveDTO dto
    ) {
        weightService.updateRecord(id, dto);
        return ApiResult.success();
    }

    @DeleteMapping("/records/{id}")
    public ApiResult<Void> deleteRecord(@PathVariable Long id) {
        weightService.deleteRecord(id);
        return ApiResult.success();
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<String> uploadAvatar(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BizException("请选择头像文件");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new BizException("头像不能超过8MB");
        }
        String extension = IMAGE_EXTENSIONS.get(file.getContentType());
        if (extension == null) {
            throw new BizException("头像仅支持 JPG、PNG 或 WebP 格式");
        }
        Path directory = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(directory);
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = directory.resolve(fileName).normalize();
        if (!target.startsWith(directory)) {
            throw new BizException("头像保存路径无效");
        }
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return ApiResult.success("/uploads/weight/" + fileName);
    }
}
