package com.example.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.example.services.*;
import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareServiceClient;
import com.azure.storage.file.share.ShareServiceClientBuilder;
import com.azure.storage.file.share.models.ShareFileItem;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.example.contratos.*;
import com.example.models.*;



@RestController
public class AzureController {
  
	//http://localhost:9100/azuredatatools/listFile
    private IAzureDataToolService _azureDataToolsService;

    //Si las anotaciones no estan funcionando, entonces hacer explicito las metodos
    @Autowired
	public AzureController(IAzureDataToolService azureDataToolsService)
	{
		this._azureDataToolsService=azureDataToolsService;		
	}	
	
    @PostMapping(value = "/azuredatatools/listFile" )
    //@RequestMapping( consumes  = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AzureFileItem>> AzureDataToolsFileListFile(@RequestBody AzureDataToolsRequest modelo){
        HttpStatus httpStatus = HttpStatus.OK;
        List<AzureFileItem> resultado = _azureDataToolsService.listFiles(modelo.getShareName(), modelo.getResourcePath());
        if(resultado==null){
            httpStatus = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(resultado,httpStatus);
    }
	
    //http://localhost:9100/azure/hello
	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	public String sayHello() {
		return "Hola Mundo!";
	}
	
	//http://localhost:9100/azure_createfile
	@RequestMapping(method = RequestMethod.GET, value = "/azure_createfile")
	public String Azure_CreateFile() {
		
		String _storageConnectionString = "BlobEndpoint=https://miblobstoragedisrupting1.blob.core.windows.net/;QueueEndpoint=https://miblobstoragedisrupting1.queue.core.windows.net/;FileEndpoint=https://miblobstoragedisrupting1.file.core.windows.net/;TableEndpoint=https://miblobstoragedisrupting1.table.core.windows.net/;SharedAccessSignature=sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2023-12-09T06:10:06Z&st=2023-12-08T22:10:06Z&spr=https&sig=46XCKBFqQqKpPUBY%2B9zOGe2zEEH1914RUhx6e3DpGf4%3D";
        String _BlogStorage = "mi-contenedor1";
        String result = "";
        String filePath = "C:\\Users\\IsraelContreras\\Downloads\\Imagen_Ejemplo_Azure.png";
        String blobName = "Imagen_Ejemplo_Azure.png";
        
        try
        {
        	BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                    .connectionString(_storageConnectionString)
                    .containerName(_BlogStorage)
                    .buildClient();

            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.uploadFromFile(filePath);            
            System.out.println("El archivo se ha subido correctamente al contenedor de blobs.");

            result += "OK (200)";
        }        
        catch(Exception Ex)
        {
        	result += "error: " + Ex.getMessage();
        }
		
		return result;
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "/azure_listfiles")
	public String AzureListFiles() {
	    String _storageConnectionString = "BlobEndpoint=https://miblobstoragedisrupting1.blob.core.windows.net/;SharedAccessSignature=sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2023-12-09T06:10:06Z&st=2023-12-08T22:10:06Z&spr=https&sig=46XCKBFqQqKpPUBY%2B9zOGe2zEEH1914RUhx6e3DpGf4%3D";
	    String _blogStorage = "mi-contenedor1";
	    String result = "";

	    try {
	        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
	                .connectionString(_storageConnectionString)
	                .containerName(_blogStorage)
	                .buildClient();

	        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
	            result += "Blob Name: " + blobItem.getName() + "<br>";
	        }
	    } catch (Exception ex) {
	        result += "Error: " + ex.getMessage();
	    }

	    return result;
	}
	
}
