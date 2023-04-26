package com.b2c.vehicle.accessories.upload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class UploadFormRequest {

    private MultipartFile file;
    private List<MultipartFile> files;
    private String title;
    private String description;
    private String uploadType;

}
