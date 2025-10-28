package com.crm.group7.runner;

import com.crm.group7.service.ImportazioneAnagraficaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class AnagraficaRunner implements CommandLineRunner {

    @Autowired
    private ImportazioneAnagraficaService importazioneAnagraficaService;
    
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {

        Resource provinceResource = resourceLoader.getResource("classpath:province-italiane.csv");
        Resource comuniResource = resourceLoader.getResource("classpath:comuni-italiani.csv");

        String provincePath = provinceResource.getFile().getAbsolutePath();
        String comuniPath = comuniResource.getFile().getAbsolutePath();

        System.out.println("DEBUG: " + provincePath);
        System.out.println("DEBUG: " + comuniPath);

        importazioneAnagraficaService.importaDatiGeografici(provincePath, comuniPath);
    }
}
