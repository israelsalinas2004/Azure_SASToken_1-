package com.example.contratos;

import java.util.List;

import com.example.models.AzureFileItem;
import com.example.models.Resultado;

public interface IAzureDataToolService {
    Resultado downloadFile(String shareName, String fileName);
    List<AzureFileItem> listFiles(String shareName, String resourcePath);
}

