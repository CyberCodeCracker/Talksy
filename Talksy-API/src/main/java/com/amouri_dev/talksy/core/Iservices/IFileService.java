package com.amouri_dev.talksy.core.Iservices;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    String saveFile(MultipartFile file, Long senderId);
}
