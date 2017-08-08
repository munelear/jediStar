package fr.jedistar.usedapis;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SheetsAPIBuilder{

    private final String APPLICATION_NAME ="Bot JediStar";

    private final java.io.File DATA_STORE_DIR = new java.io.File(".credentials/sheets.googleapis.com-jediStarBot");
    
    private InputStream in = SheetsAPIBuilder.class.getResourceAsStream("/client_secret.json");

    private FileDataStoreFactory DATA_STORE_FACTORY;

    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private HttpTransport HTTP_TRANSPORT;
    
    private Sheets sheetsAPI = null;

    private final List<String> SCOPES_READONLY = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);
    private final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

    private boolean readOnly;
    	
    private String sheetID;
  
    public SheetsAPIBuilder(String sheetId,boolean readonly) throws IOException, GeneralSecurityException {

    	HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    	DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

    	GoogleCredential credential = null;

    	if(readonly) {
    		credential = GoogleCredential.fromStream(in).createScoped(SCOPES_READONLY);
    	}
    	else {
    		credential = GoogleCredential.fromStream(in).createScoped(SCOPES);
    	}

    	sheetsAPI = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

    	this.sheetID = sheetId;
    	this.readOnly = readonly;
    }

    
    public List<List<Object>> getRange(String range) throws IOException{
    	
    	ValueRange response = sheetsAPI.spreadsheets().values().get(sheetID,range).execute();
    	
    	return response.getValues();
    }

    public void write(String range,List<List<Object>> data) throws IOException {

    	if(readOnly) {
    		throw new UnsupportedOperationException("Trying to write to Google Sheets using a readonly instance of Sheets API");
    	}

    	ValueRange oRange = new ValueRange();
    	oRange.setRange(range);
    	oRange.setValues(data);

    	List<ValueRange> oList = new ArrayList<>();
    	oList.add(oRange);

    	BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
    	oRequest.setValueInputOption("RAW");
    	oRequest.setData(oList);


    	BatchUpdateValuesResponse oResp1 = sheetsAPI.spreadsheets().values().batchUpdate(sheetID, oRequest).execute();
    }
    
    public Integer readInteger(Object valueFromSheet) {
    	if(valueFromSheet instanceof Integer) {
    		return (Integer)valueFromSheet;
    	}
    	else if(valueFromSheet instanceof String) {
    		return Integer.parseInt((String)valueFromSheet);
    	}
    	else {
    		return null;
    	}
    }
}
