package com.example.services;

import com.azure.core.http.rest.PagedIterable;
import com.azure.json.implementation.jackson.core.JsonProcessingException;
//import com.azure.storage.file.share.ShareDirectoryClientBuilder;
import com.azure.storage.file.share.*;
import com.azure.storage.file.share.models.ShareFileItem;
import com.azure.storage.file.share.models.ShareFileProperties;

import java.io.Console;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.example.contratos.*;
import com.example.models.*;
import com.example.models.AzureFileItem;
import com.example.models.Resultado;
import com.fasterxml.jackson.databind.ObjectMapper;

import aj.org.objectweb.asm.TypeReference;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AzureDataToolsService implements IAzureDataToolService {
	
    String fileShareURL = "https://miblobstoragedisrupting1.file.core.windows.net/mifile1";
    String URLSAS_FileStorage = "https://miblobstoragedisrupting1.file.core.windows.net/?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2023-12-09T03:49:25Z&st=2023-12-08T19:49:25Z&spr=https,http&sig=eF5a8aeKsu%2BXUNkXB78AILPXBvtb%2BNKiqnt%2FRblZBGE%3D"; 
    String sasToken = "?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2023-12-09T01:39:48Z&st=2023-12-08T17:39:48Z&spr=https&sig=NR0VAFqHqi6TvmE%2Fee4rITuPdqZ3M1IKpZR%2BRLNZuuc%3D";
     
	@Override
	public List<AzureFileItem> listFiles(String shareName, String resourcePath) {
        List<AzureFileItem> resultado =new ArrayList<>();
        try{
        	//Verificar que si esta llegando la información.
        	//System.out.println("shareName: " + shareName);
        	//System.out.println("resourcePath: " + resourcePath);       	
          	
        	//Genero la conexion
            ShareServiceClient shareServiceClient = new ShareServiceClientBuilder()
                    .sasToken(sasToken)
                    .endpoint(shareName)
                    .buildClient();
         
            //tomos los archivos   
            ShareClient shareClient = shareServiceClient.getShareClient(resourcePath);
            ShareDirectoryClient directoryClient = shareClient.getRootDirectoryClient();
            PagedIterable<ShareFileItem> elements = directoryClient.listFilesAndDirectories();
                   
            System.out.println("AQUI LLEGO 1 "); 
            
            try {
                if (elements != null) {
                	
                    for (ShareFileItem item : elements) {
                        if (!item.isDirectory()) {
                            String fileName = item.getName();
                            ShareFileClient shareFileClient = directoryClient.getFileClient(fileName);

                            ShareFileProperties fileItemProperties = shareFileClient.getProperties();
                            OffsetDateTime lastModified = fileItemProperties.getLastModified();
                            
                            AzureFileItem fileItem = new AzureFileItem();
                            fileItem.setFileName(fileName);
                            fileItem.setLastModified(lastModified);
                            
                            resultado.add(fileItem);	
                        }
                    }

                	
                	/*
                    for (ShareFileItem item : elements) {
                      	System.out.println("AQUI LLEGO 2A:  " + item.getName());
                    	
                        System.out.println("Nombre: " + item.getName());
                        System.out.println("Tipo: " + (item.isDirectory() ? "Directorio" : "Archivo"));
                      	
                      	
                        if(item.isDirectory())continue;
                        String fileName = resourcePath + "/" + item.getName();
                        System.out.println("AQUI LLEGO 2B ");
                        ShareFileClient shareFileClient = getShareFileClient(shareName,fileName);

                        ShareFileProperties fileItemProperties = shareFileClient.getProperties();
                        OffsetDateTime lastModified = fileItemProperties.getSmbProperties().getFileChangeTime();
                        OffsetDateTime createdOn = fileItemProperties.getSmbProperties().getFileCreationTime();
                        AzureFileItem fileItem =new AzureFileItem();     
                        fileItem.setFileName(item.getName());
                        fileItem.setCreatedOn(createdOn);
                        fileItem.setLastModified(lastModified);
                        resultado.add(fileItem);	
                    }
                    */
                } else {
                    // Manejo en caso de que elements sea nulo
                	System.out.println("List<AzureFileItem> listFiles: elements viene nulo: ex_1 ");	
                }
            } 
            catch (Exception ex_2) {
            	System.out.println("List<AzureFileItem> listFiles: elements / Ex_2. " + ex_2.getMessage());
            }

        }catch (Exception ex_3){
        	System.out.println("List<AzureFileItem> listFiles: elements / Ex_3. " + ex_3.getMessage());
        }
        
        
        return resultado;
	}
	
	public ShareFileClient getShareFileClient(String shareName, String resourcePath){

	    try 
	    {
	        // Construir la URL base para el recurso compartido
	        String shareBaseURL = "" + shareName + ".file.core.windows.net";

            System.out.println("METODO: getShareFileClient / shareBaseURL: " + shareBaseURL + "/");        	
	        
	        // Construir el cliente del archivo compartido
	        ShareFileClient fileClient = new ShareFileClientBuilder()
	                .endpoint(shareBaseURL)
	                .sasToken(sasToken)
	                .shareName(resourcePath) // Usar el recursoPath como nombre del recurso compartido
	                .buildFileClient();

	        return fileClient;
	    } catch (Exception ex) {
	        // Manejo de excepciones aquí
	        throw ex;
	    }
	    
    }
	
    public ShareClient getShareClient(String shareName){
    	
    	String sasToken = "https://miblobstoragedisrupting1.blob.core.windows.net/?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2023-12-31T06:17:20Z&st=2023-11-30T22:17:20Z&spr=https&sig=XpinYE%2FGJ0G%2BPJ9ewtKO%2FqyeAAdgzNxCT4g5VDADl0k%3D";
        String shareBaseURL = "" + shareName + ".file.core.windows.net";
    	
        System.out.println("METODO: getShareClient / shareBaseURL: " + shareBaseURL + "/ shareName: "+ shareName);
        
        return new ShareClientBuilder()
                .shareName(shareName)
                .sasToken(sasToken)
                .buildClient();
    }

	@Override
	public Resultado downloadFile(String shareName, String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
