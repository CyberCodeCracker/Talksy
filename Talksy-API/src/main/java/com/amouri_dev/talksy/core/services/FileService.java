package com.amouri_dev.talksy.core.services;

import com.amouri_dev.talksy.core.Iservices.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {


    @Override
    public String  saveFile(MultipartFile file, Long senderId) {
        return null;
    }
}
