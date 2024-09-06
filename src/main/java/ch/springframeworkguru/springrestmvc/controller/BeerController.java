package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.service.BeerService;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${controllers.beer-controller.request-path}")
@Slf4j
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @DeleteMapping(value="/deleteBeer/{beerId}")
    public ResponseEntity<BeerDTO> deleteBeer(@PathVariable("beerId") UUID beerId) {
        BeerDTO deletedBeer = beerService.deleteBeer(beerId);
        return new ResponseEntity<>(deletedBeer, HttpStatus.OK);
    }

    @GetMapping(value="/listBears")
    public List<BeerDTO> listBeers(){
        return beerService.listBeers();
    }

    @GetMapping(value = "/getBeerById/{beerId}")
    public ResponseEntity<BeerDTO> getBeerById(@PathVariable("beerId") UUID beerId){
        return new ResponseEntity<>(beerService.getBeerById(beerId).orElseThrow(NotfoundException::new), HttpStatus.OK);
    }

    @PostMapping(value = "/createBeer")
    public ResponseEntity<BeerDTO> createBeer(@RequestBody BeerDTO newBeer) {
        BeerDTO beer = beerService.saveNewBeer(newBeer);
        return new ResponseEntity<>(beer, HttpStatus.CREATED);
    }

    @PutMapping(value = "/editBeer/{beerId}")
    public ResponseEntity<BeerDTO> editBeer(@RequestBody BeerDTO beer, @PathVariable("beerId") UUID beerId) {
        BeerDTO updatedBeer = beerService.editBeer(beerId, beer);
        return new ResponseEntity<>(updatedBeer, HttpStatus.OK);
    }

    @PatchMapping(value = "/patchBeer/{beerId}")
    public ResponseEntity<BeerDTO> patchBeer(@RequestBody BeerDTO beer, @PathVariable("beerId") UUID beerId) {
        BeerDTO patchedBeer = beerService.patchBeer(beerId, beer);
        return new ResponseEntity<>(patchedBeer, HttpStatus.OK);
    }
}
