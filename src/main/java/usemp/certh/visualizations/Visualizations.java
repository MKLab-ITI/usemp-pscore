/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.visualizations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import usemp.certh.main.Main;

/**
 *
 * @author gpetkos
 * 
 * This class just prepares and copies the files for the visualization.
 * 
 */
public class Visualizations {
    public void copyVisualizationFiles(String targetDir){
        URL url = this.getClass().getClassLoader().getResource("");
        File resourcesFile = null;
        String resourcesDir=null;
        try {
            resourcesFile = new File(url.toURI());
        } catch (URISyntaxException e) {
            resourcesFile = new File(url.getPath());
        } finally {
            resourcesDir=resourcesFile.getAbsolutePath();
            if((!resourcesDir.endsWith("/"))&&(!resourcesDir.endsWith("\\")))
                resourcesDir=resourcesDir+"/";
        }
        
        try {
            FileUtils.copyDirectoryToDirectory(new File(resourcesDir+"visualizations/"), new File(targetDir));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        /*
        //First prepare the files for the 'advanced' scoring visualizations
        String fileOut=targetDir+"visualizations/js/advanced/plot.js";
        String fileHead=targetDir+"visualizations/js/advanced/plot_head";
        String fileJson=targetDir+"myScores.json";
        String fileTail=targetDir+"visualizations/js/advanced/plot_tail";
        
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileOut), "UTF8"));
            
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileHead), "UTF8"));
            String line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileJson), "UTF8"));
            line=br.readLine();
            br.close();
            String escaped=StringEscapeUtils.escapeJavaScript(line);
            //bw.append("'{\"data\":");
//            bw.append(escaped);
            bw.append(line);
            //bw.append("}';");
            bw.newLine();
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileTail), "UTF8"));
            line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            bw.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        */
        //Then prepare the files for the 'simple' scoring visualization
        String fileOut=targetDir+"visualizations/js/advanced/plot.js";
        String fileHead=targetDir+"visualizations/js/advanced/plot_head";
        String fileJson=targetDir+"myScores.json";
        String fileTail=targetDir+"visualizations/js/advanced/plot_tail";
        
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileOut), "UTF8"));
            
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileHead), "UTF8"));
            String line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileJson), "UTF8"));
            line=br.readLine();
            br.close();
            String escaped=StringEscapeUtils.escapeJavaScript(line);
            //bw.append("'{\"data\":");
//            bw.append(escaped);
            bw.append(line);
            //bw.append("}';");
            bw.newLine();
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileTail), "UTF8"));
            line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            bw.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }

        //Then prepare the files for the 'plain control' visualization
        fileOut=targetDir+"visualizations/js/wall_plain/main.js";
        fileHead=targetDir+"visualizations/js/wall_plain/main_head";
        fileJson=targetDir+"myControlSuggestionSetPlain.json";
        fileTail=targetDir+"visualizations/js/wall_plain/main_tail";
        
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileOut), "UTF8"));
            
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileHead), "UTF8"));
            String line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileJson), "UTF8"));
            line=br.readLine();
            br.close();
            String escaped=StringEscapeUtils.escapeJavaScript(line);
            //bw.append("'{\"data\":");
//            bw.append(escaped);
            bw.append(line);
            //bw.append("}';");
            bw.newLine();
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileTail), "UTF8"));
            line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            bw.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        //Then prepare the files for the 'extended control' visualization
        fileOut=targetDir+"visualizations/js/wall_extended/main.js";
        fileHead=targetDir+"visualizations/js/wall_extended/main_head";
        fileJson=targetDir+"myControlSuggestionSetExtended.json";
        fileTail=targetDir+"visualizations/js/wall_extended/main_tail";
        
        try{
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileOut), "UTF8"));
            
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileHead), "UTF8"));
            String line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileJson), "UTF8"));
            line=br.readLine();
            br.close();
            String escaped=StringEscapeUtils.escapeJavaScript(line);
            //bw.append("'{\"data\":");
//            bw.append(escaped);
            bw.append(line);
//            bw.append("}';");
            bw.newLine();
            br.close();
            
            br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileTail), "UTF8"));
            line=null;
            while((line=br.readLine())!=null){
                bw.append(line);
                bw.newLine();
            }
            br.close();
            bw.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
