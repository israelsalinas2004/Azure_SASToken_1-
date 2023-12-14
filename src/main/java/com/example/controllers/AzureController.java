package com.example.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.services.*;
import com.microsoft.applicationinsights.core.dependencies.apachecommons.io.output.ByteArrayOutputStream;

import jakarta.persistence.criteria.Path;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.example.contratos.*;
import com.example.models.*;
import java.io.InputStream;
import java.io.ByteArrayInputStream;



@RestController
public class AzureController {
  
	// DOCUMENTACION
	// https://learn.microsoft.com/en-us/azure/storage/common/storage-samples-java
	// POM.XML ==> 13/12/2024 : ERROR  Empezo a funcionar hasta que te fuiste a una version anterior ..12.10.2
	// ERRO: Reactor.Core ==> ; colocar exclusions en pom.xml https://learn.microsoft.com/en-us/answers/questions/1188887/error-occurs-java-lang-nosuchmethoderror-reactor-c
	
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
    public AzureController(IAzureDataToolService azureDataToolsService)
	{
		this._azureDataToolsService=azureDataToolsService;		
	}	
    
    //http://localhost:9100/azure/hello
	@GetMapping("/hello")
	public String sayHello() {
		return "Hola Mundo!";
	}
	
	
    @PostMapping(value = "/azuredatatools/omp_listfiles" )
    //@RequestMapping( consumes  = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AzureFileItem>> AzureDataToolsFileListFile(@RequestBody AzureDataToolsRequest modelo){
        HttpStatus httpStatus = HttpStatus.OK;
        List<AzureFileItem> resultado = _azureDataToolsService.listFiles(modelo.getShareName(), modelo.getResourcePath());
        if(resultado==null){
            httpStatus = HttpStatus.NO_CONTENT;
        }
        return new ResponseEntity<>(resultado,httpStatus);
    }
	

	//http://localhost:9100/azure_listfiles_containers
	@PostMapping("/azure_listfiles_containers")
	public String AzureListFiles_Containers() {
		
		StringBuilder result = new StringBuilder();	

		try
		{	
			String accountName = "miblobstoragedisrupting1";
	        String accountKey = "K8tAgI7Kuzr4+qpn9wxVe5fkQI2i386txdtpiAnrDSFvPAVJbN9nL0d1PUpmSgbzIg5bJaS0sbxv+AStU14NdQ==";
			StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
			String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);
			
			BlobServiceClient storageClient = new BlobServiceClientBuilder()
												  .endpoint(endpoint)
												  .credential(credential)
												  .buildClient();
			
	        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient("mi-contenedor1");
	        System.out.println("Blob name: " + blobContainerClient.exists());
	        
	        
	        blobContainerClient.listBlobs()
            .forEach(blobItem -> 
            	result.append("\nBlob name: " + blobItem.getName()+ ", Snapshot: " + blobItem.getSnapshot())
            );

		}
		catch(Exception ex) {
			result.append("\tError: " + ex.getMessage());
		}

        //result.append("\t" + blobItem.getName());
	    return result.toString();
	}

    
	//http://localhost:9100/azure_createfile
	@GetMapping("/azure_createfile_container")
	public String Azure_CreateFile() {
		
        String result = "";
        String filePath = "C:\\Users\\IsraelContreras\\Downloads\\Imagen_Ejemplo_Azure.png";
        String blobName_file = "Imagen_Ejemplo_Azure.png";
        
        try
        {
			String accountName = "miblobstoragedisrupting1";
	        String accountKey = "K8tAgI7Kuzr4+qpn9wxVe5fkQI2i386txdtpiAnrDSFvPAVJbN9nL0d1PUpmSgbzIg5bJaS0sbxv+AStU14NdQ==";
			StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
			String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);
						
			BlobServiceClient storageClient = new BlobServiceClientBuilder()
												  .endpoint(endpoint)
												  .credential(credential)
												  .buildClient();
			
	        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient("mi-contenedor1");

	        BlockBlobClient blobClient = blobContainerClient
	        							 .getBlobClient("Ejemplo de archivo creado.txt")
	        							 .getBlockBlobClient();

	        String data = "Hello world!";
	        InputStream dataStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
	        blobClient.upload(dataStream, data.length());

	        dataStream.close();
	        
	        
	        
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
	@PostMapping("/azure_downloadfile")
	public String Azure_DownloadFile(@RequestBody RequestData requestData) throws IOException {
		
		String filename = requestData.getFilename();
        String result = "";
        String filePath = "C:\\Users\\IsraelContreras\\Downloads\\";
        
        try
        {
			String accountName = "miblobstoragedisrupting1";
	        String accountKey = "K8tAgI7Kuzr4+qpn9wxVe5fkQI2i386txdtpiAnrDSFvPAVJbN9nL0d1PUpmSgbzIg5bJaS0sbxv+AStU14NdQ==";
			StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
			String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);
			
			System.out.println("Parametro recibido: " + filename);
			
			BlobServiceClient storageClient = new BlobServiceClientBuilder()
												  .endpoint(endpoint)
												  .credential(credential)
												  .buildClient();
			
	        BlobContainerClient blobContainerClient = storageClient.getBlobContainerClient("mi-contenedor1");
	        BlobClient blobClient = blobContainerClient.getBlobClient(filename);
	        
	        //Guarda archivo
	        String localFilePath = filePath + filename;
			blobClient.downloadToFile(localFilePath);
			System.out.println("Archivo descargado exitosamente.");	        
        	
            result += "OK (200)";
        }        
        catch(Exception Ex)
        {
        	result += "error: " + Ex.getMessage();
        }
		
		return result;
	}

	
    //http://localhost:9100/salesforce_authentication?token=12345
    @GetMapping("/salesforce_authentication")
    public RedirectView salesforceAuthentication(@RequestParam String token) {
        String redirectURL = "https://omp-uat-nt.azurewebsites.net/login?token=" + token;
        
        return new RedirectView(redirectURL);
    }
	
}
