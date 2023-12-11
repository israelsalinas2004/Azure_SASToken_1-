package com.example.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.microsoft.applicationinsights.core.dependencies.apachecommons.io.output.ByteArrayOutputStream;
import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareServiceClient;
import com.azure.storage.file.share.ShareServiceClientBuilder;
import com.azure.storage.file.share.models.ShareFileItem;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.example.contratos.*;
import com.example.models.*;



@RestController
public class AzureController {
  
	//http://localhost:9100/azuredatatools/listFile
    private IAzureDataToolService _azureDataToolsService;
    
    
    String _storageConnectionString = "BlobEndpoint=https://miblobstoragedisrupting1.blob.core.windows.net/;QueueEndpoint=https://miblobstoragedisrupting1.queue.core.windows.net/;FileEndpoint=https://miblobstoragedisrupting1.file.core.windows.net/;TableEndpoint=https://miblobstoragedisrupting1.table.core.windows.net/;SharedAccessSignature=sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-21T01:15:19Z&st=2023-12-11T17:15:19Z&spr=https,http&sig=Gdkrm%2B7suc4hA2ASEsMpizyxZ3vszQXH4t81rF%2BYaeU%3D";
    String _sasToken = "?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-21T01:15:19Z&st=2023-12-11T17:15:19Z&spr=https,http&sig=Gdkrm%2B7suc4hA2ASEsMpizyxZ3vszQXH4t81rF%2BYaeU%3D";
    String _URL_SAS_BlobService = "https://miblobstoragedisrupting1.blob.core.windows.net/?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-21T01:15:19Z&st=2023-12-11T17:15:19Z&spr=https,http&sig=Gdkrm%2B7suc4hA2ASEsMpizyxZ3vszQXH4t81rF%2BYaeU%3D";
    String _URL_SAS_FileService = "https://miblobstoragedisrupting1.file.core.windows.net/?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-21T01:15:19Z&st=2023-12-11T17:15:19Z&spr=https,http&sig=Gdkrm%2B7suc4hA2ASEsMpizyxZ3vszQXH4t81rF%2BYaeU%3D";
    String _URL_SAS_QueuService = "https://miblobstoragedisrupting1.queue.core.windows.net/?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-21T01:15:19Z&st=2023-12-11T17:15:19Z&spr=https,http&sig=Gdkrm%2B7suc4hA2ASEsMpizyxZ3vszQXH4t81rF%2BYaeU%3D";
    String _URL_SAS_TableService = "https://miblobstoragedisrupting1.table.core.windows.net/?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-21T01:15:19Z&st=2023-12-11T17:15:19Z&spr=https,http&sig=Gdkrm%2B7suc4hA2ASEsMpizyxZ3vszQXH4t81rF%2BYaeU%3D";
    
    String _blogStorage_ContainerName = "mi-contenedor1";
    

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
	

	//http://localhost:9100/azure_listfiles
	@RequestMapping(method = RequestMethod.GET, value = "/azure_listfiles")
	public String AzureListFiles() {

		StringBuilder result = new StringBuilder();
		
	    try 
	    {
        	BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                    .connectionString(_storageConnectionString)
                    .containerName(_blogStorage_ContainerName)
                    .buildClient();
        	
	    	for (BlobItem  blobItem  :  blobContainerClient.listBlobs()) {
	    		result.append("\t" + blobItem.getName());
	    	}
        	
	    	/*
        	BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                    .connectionString(_storageConnectionString)
                    .containerName(_blogStorage_ContainerName)
                    .buildClient();
	    	 
	    	
	    	for (BlobItem  blobItem  :  blobContainerClient.listBlobs()) {
	    		result.append("\t" + blobItem.getName());
	    	}
	    	*/
		        
	    } catch (Exception ex) {
	        result.append("Error: ").append(ex.getMessage());
	    }

	    return result.toString();
	}
	

	//http://localhost:9100/azure_createfile
	@RequestMapping(method = RequestMethod.GET, value = "/azure_createfile")
	public String Azure_CreateFile() {
		
        String result = "";
        String filePath = "C:\\Users\\IsraelContreras\\Downloads\\Imagen_Ejemplo_Azure.png";
        String blobName_file = "Imagen_Ejemplo_Azure.png";
        
        try
        {
        	BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                    .connectionString(_storageConnectionString)
                    .containerName(_blogStorage_ContainerName)
                    .buildClient();

            BlobClient blobClient = blobContainerClient.getBlobClient(blobName_file);
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
	

	//http://localhost:9100/azure_downloadfile
	@RequestMapping(method = RequestMethod.GET, value = "/azure_downloadfile")
	public String Azure_DownloadFile(String filenametodownload) {
		
        String result = "";
        String filePath = "C:\\Users\\IsraelContreras\\Downloads\\";
        
        try
        {
        	System.out.println("Archivo a descargar:" +  filenametodownload);
        	
        	BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                    .connectionString(_storageConnectionString)
                    .containerName(_blogStorage_ContainerName)
                    .buildClient();
        	
        
        	//Tomo el achivo
            BlobClient blobClient = blobContainerClient.getBlobClient(filenametodownload);
 
            //lo guardo en un objeto
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.downloadStream(outputStream);		
            
            //Si lo quiero guardar en una localidad fisica
            FileOutputStream fileOutputStream = new FileOutputStream(filePath+filenametodownload);
            outputStream.writeTo(fileOutputStream);
        	 
        	
            result += "OK (200)";
        }        
        catch(Exception Ex)
        {
        	result += "error: " + Ex.getMessage();
        }
		
		return result;
	}

	
	
}
