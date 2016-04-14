/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usemp.certh.scoring;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author gpetkos
 */
public class Test {
    public static void main(String[] args){
        DisclosureScoringFramework dsf=new DisclosureScoringFramework();
//        User user=new User("giorgos");
        ScoringUser user=dsf.getUser("giorgos");
        user.addDimension("demographics",null);
        user.addAttribute("demographics","age",true,null);
        user.addDimension("demographics",null);
        user.addAttribute("demographics","ethnicity",true,null);
        user.addAttribute("religious","religious",true,null);
        dsf.saveUser(user);
        
        user=dsf.getUser("giorgos");
        if(user==null)
            System.out.println("User does not exist");
        else
            System.out.println(user.toJSonStringPretty());
    }

    public static void testUser(){
        ScoringUser user=new ScoringUser("giorgos");
        user.addDimension("demographics",null);
        user.addAttribute("demographics","age",true,null);
        user.addAttribute("religious","religious",true,null);
        
        
    }
    
}
