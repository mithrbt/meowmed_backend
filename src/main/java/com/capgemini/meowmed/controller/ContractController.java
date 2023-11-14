package com.capgemini.meowmed.controller;

import com.capgemini.meowmed.enums.Color;
import com.capgemini.meowmed.enums.Environment;
import com.capgemini.meowmed.exception.ResourceNotFoundException;
import com.capgemini.meowmed.model.*;
import com.capgemini.meowmed.repository.ContractRepository;
import com.capgemini.meowmed.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class ContractController {

    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private CustomerRepository customerRepository;

    //Get all contracts
    @GetMapping("/kunden/{customerID}/vertrag")
    public List<Contract> getAllContractsByCustomerID(@PathVariable int customerID){
        return contractRepository.findByCustomerId(customerID);
    }

    //get contract by ID
    @GetMapping("/vertrag/{contractID}")
    public ResponseEntity<Contract> getContractById(@PathVariable int contractID) throws ResourceNotFoundException{

        Contract contract = contractRepository.findById(contractID)
                .orElseThrow(() -> new ResourceNotFoundException("Es gibt keinen Vertrag mit der ID: " + contractID));

        return ResponseEntity.ok().body(contract);
    }

    //create contract
    @PostMapping("/kunden/{customerID}/vertrag")
    public ResponseEntity<Contract> createContract(@PathVariable int customerID, @Valid @RequestBody Contract contractRequest){
        Contract contract = customerRepository.findById(customerID).map(customer -> {
            contractRequest.setCustomer(customer);
            return contractRepository.save(contractRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Es gibt keinen Kunden mit der ID: " + customerID));

        return new ResponseEntity<>(contract, HttpStatus.CREATED);
    }


    //update contract
    @PutMapping("/vertrag/{contractID}")
    public ResponseEntity<Contract> updateContract(@PathVariable int contractID, @Valid @RequestBody Contract contractRequest) throws ResourceNotFoundException{
        Contract contract = contractRepository.findById(contractID)
                .orElseThrow(() -> new ResourceNotFoundException("Es gibt keinen Vertrag mit der ID: " + contractID));

        contract.setCustomer(contractRequest.getCustomer());
        contract.setCoverage(contractRequest.getCoverage());
        contract.setEnd(contractRequest.getEnd());
        contract.setStart(contractRequest.getStart());

        final Contract updateContract = contractRepository.save(contract);
        return ResponseEntity.ok(updateContract);
    }

    //delete contract
    @DeleteMapping("vertrag/{contractID}")
    public void deleteContract(@PathVariable int contractID) throws ResourceNotFoundException{
        contractRepository.deleteById(contractID);
    }

    static class Catract{
        public Cat cat;
        public Contract contract;
    }

    @PostMapping("vertrag/quote")
    public double quote(@Valid @RequestBody Catract catract){
        double quote = 5;
        System.out.println(catract.cat.getColor());
        if(catract.cat.getColor() == Color.SCHWARZ){
            quote += catract.contract.getCoverage() * 0.2;
            if(catract.cat.getEnvironment() == Environment.DRAUSSEN){
                quote *= 1.1;
            }
            if(!catract.cat.isCastrated()){
                quote += 5;
            }
            return quote;
        }
        quote += catract.contract.getCoverage() * 0.15;
        if(catract.cat.getEnvironment() == Environment.DRAUSSEN){
            quote *= 1.1;
        }
        if(!catract.cat.isCastrated()){
            quote += 5;
        }

        return quote;
    }


}
