package com.robotgame.controller;

import com.robotgame.dto.incoming.LegionCreationDTO;
import com.robotgame.dto.outgoing.LegionNameQuantityListDTO;
import com.robotgame.dto.outgoing.UnitListDTO;
import com.robotgame.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<Void> unitBuyer(@RequestBody LegionCreationDTO LCDTO) {
        marketService.marketBuyUnit(LCDTO.getUnit(), LCDTO.getQuantity());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/sell")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<Void> unitSeller(@RequestBody LegionCreationDTO LCDTO) {
        marketService.marketSellUnit(LCDTO.getUnit(), LCDTO.getQuantity());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/units")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<List<UnitListDTO>> marketUnitLister() {
        return new ResponseEntity<>(marketService.unitMarketLister(), HttpStatus.OK);
    }

    @GetMapping("/ownUnitsQuantity")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ResponseEntity<List<LegionNameQuantityListDTO>> ownUnitsQuantityLister(){
        return new ResponseEntity<>(marketService.legionsQuantityLister(), HttpStatus.OK);
    }

}
