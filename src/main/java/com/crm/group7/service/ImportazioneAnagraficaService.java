package com.crm.group7.service;

import com.crm.group7.entities.Comune;
import com.crm.group7.entities.Provincia;
import com.crm.group7.repositories.ComuneRepository;
import com.crm.group7.repositories.ProvinciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ImportazioneAnagraficaService {

    private final ProvinciaRepository provinciaRepository;
    private final ComuneRepository comuneRepository;

    public ImportazioneAnagraficaService(ProvinciaRepository provinciaRepository, ComuneRepository comuneRepository) {
        this.provinciaRepository = provinciaRepository;
        this.comuneRepository = comuneRepository;
    }

    private static final Set<String> EXCEL_ERROR_STRINGS = Set.of(
            "#RIF!", "#VALORE!", "#NOME?", "#NUM!", "#DIV/0!", "#N/D"
    );

    private static String cleanIfExcelError(String value) {
        if (value == null) {
            return null;
        }
        String trimmedValue = value.trim().toUpperCase();

        if (EXCEL_ERROR_STRINGS.contains(trimmedValue)) {
            return null;
        }
        return value;
    }

    private static final Map<String, String> PROVINCE_CORRECTION_MAP;

    static {
        Map<String, String> corrections = new HashMap<>();

        corrections.put("MASSA CARRARA", "Massa-Carrara");
        corrections.put("BOLZANO/BOZEN", "Bolzano");
        corrections.put("AOSTA", "Aosta");
        corrections.put("REGGIO NELL'EMILIA", "Reggio-Emilia");
        corrections.put("F. BARLETTA-ANDRIA-TRANI", "Barletta-Andria-Trani");

        // CORREZIONI AGGIUNTE DAL LOG
        corrections.put("VERBANO-CUSIO-OSSOLA", "Verbania");
        corrections.put("VALLE D'AOSTA/VALLÉE D'AOSTE", "Aosta");
        corrections.put("MONZA E DELLA BRIANZA", "Monza-Brianza");
        corrections.put("LA SPEZIA", "La-Spezia");
        corrections.put("REGGIO CALABRIA", "Reggio-Calabria");
        corrections.put("VIBO VALENTIA", "Vibo-Valentia");
        corrections.put("SUD SARDEGNA", "Carbonia Iglesias");
        corrections.put("PESARO E URBINO", "Pesaro-Urbino");
        corrections.put("ASCOLI PICENO", "Ascoli-Piceno");
        corrections.put("FORLÌ-CESENA", "Forli-Cesena");

        PROVINCE_CORRECTION_MAP = Collections.unmodifiableMap(corrections);
    }

    @Transactional
    public void importaDatiGeografici(String provinceFilePath, String comuniFilePath) {

        // FASE 1: Importa/Carica le Province in cache
        Map<String, Provincia> provinceCache = importaProvince(provinceFilePath);
        System.out.printf("FASE 1 COMPLETATA: Province caricate in cache: %d%n", provinceCache.size());

        // FASE 2: Importa i Comuni usando la cache
        importaComuni(comuniFilePath, provinceCache);

        System.out.println("FASE 2 COMPLETATA: Popolamento Anagrafica Geografica completato con successo!");
    }

    private Map<String, Provincia> importaProvince(String filePath) {
        Map<String, Provincia> provinceMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {

            br.readLine(); // Salta l'intestazione

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 2) continue;

                String inputSigla = values[0].trim();
                String nome = values[1].trim();

                String sigla;

                // 1. Logica di correzione SIGLA per anomalie di lunghezza
                if (inputSigla.length() > 2) {
                    if (inputSigla.equalsIgnoreCase("Roma") && nome.equalsIgnoreCase("Roma")) {
                        sigla = "RM";
                    } else if (inputSigla.equalsIgnoreCase("Valle d'Aosta")) {
                        sigla = "AO";
                    } else {
                        // Sigla anomala > 2 caratteri non gestita: Forziamo la sigla, ma è un caso limite
                        System.err.println("ATTENZIONE CRITICA: Sigla anomala > 2 caratteri trovata: '" + inputSigla + "'. Forzando sigla a 2 caratteri.");
                        sigla = inputSigla.substring(0, 2);
                    }
                } else {
                    sigla = inputSigla;
                }

                // 2. Controllo Duplicati e Salvataggio: cerca prima di salvare
                Provincia p;

                Provincia existing = provinciaRepository.findByNome(nome).orElse(null);

                if (existing != null) {
                    p = existing; // Se esiste, usa l'entità esistente
                } else {
                    // Salva solo se non esiste
                    Provincia newProvincia = new Provincia();
                    newProvincia.setSigla(sigla);
                    newProvincia.setNome(nome);
                    p = provinciaRepository.save(newProvincia);
                }

                // 3. Popola la cache
                provinceMap.put(p.getNome(), p);
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore critico in FASE 1 (Province): " + e.getMessage(), e);
        }
        return provinceMap;
    }

    // Nel metodo importaComuni:
    private void importaComuni(String filePath, Map<String, Provincia> provinceCache) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {

            br.readLine(); // Salta l'intestazione

            String line;
            int lineCount = 1;

            while ((line = br.readLine()) != null) {

                String[] values = line.split(";");

                if (values.length < 4) {
                    lineCount++;
                    continue;
                }

                String nomeProvinciaRaw = values[3].trim();
                String nomeComune = values[2].trim();
                String codiceProvinciaStorico = values[0].trim();
                String progressivoComune = values[1].trim();

                String nomeCacheKey = nomeProvinciaRaw;

                String progressivoPulito = cleanIfExcelError(progressivoComune);

                String nomeComunePulito = cleanIfExcelError(nomeComune);

                if (PROVINCE_CORRECTION_MAP.containsKey(nomeProvinciaRaw.toUpperCase())) {
                    nomeCacheKey = PROVINCE_CORRECTION_MAP.get(nomeProvinciaRaw.toUpperCase());
                }

                Provincia provincia = provinceCache.get(nomeCacheKey);

                if (provincia != null && nomeComunePulito != null) {
                    Comune c = new Comune();

                    // Valori puliti
                    c.setNome(nomeComunePulito);
                    c.setCodiceProvinciaStorico(codiceProvinciaStorico);
                    c.setProgressivoComune(progressivoPulito);

                    c.setProvincia(provincia);
                    comuneRepository.save(c);
                } else {
                    System.err.printf("ERRORE: Riga %d saltata a causa di Provincia mancante o Nome Comune non valido.%n", lineCount);
                }
                lineCount++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore critico in FASE 2 (Comuni): " + e.getMessage(), e);
        }
    }
}