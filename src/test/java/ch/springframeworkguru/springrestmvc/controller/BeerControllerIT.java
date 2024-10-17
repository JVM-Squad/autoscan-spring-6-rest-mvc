package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.mapper.BeerMapper;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testDeleteBeerById() {
        Beer beer = beerRepository.findAll().getFirst();
        beerController.deleteBeer(beer.getId());

        assertFalse(beerRepository.findById(beer.getId()).isPresent());
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testDeleteBeerByIdNotFound() {
        assertThrows(NotfoundException.class, () -> {
            beerController.deleteBeer(UUID.randomUUID());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testSaveBeer() {
        BeerDTO newBeerDTO = BeerDTO.builder()
                .beerName("verynewBeer")
                .beerStyle(BeerStyle.GOSE)
                .upc("upc")
                .price(BigDecimal.valueOf(55))
                .quantityOnHand(2)
                .build();

        BeerDTO createdBeer = beerController.createBeer(newBeerDTO).getBody();
        assertAll(() -> {
            assertNotNull(createdBeer);
            assertEquals("verynewBeer", createdBeer.getBeerName());
            assertNotNull(beerController.getBeerById(createdBeer.getId()));
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testUpdateExistingBeer() {
        Beer beer = beerRepository.findAll().getFirst();
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);

        beerDTO.setBeerName("UPDATED BEER");
        beerDTO.setId(null);
        beerDTO.setVersion(null);

        BeerDTO editedBeer = beerController.editBeer(beerDTO, beer.getId()).getBody();
        assertAll(() -> {
            assert editedBeer != null;
            assertEquals("UPDATED BEER", editedBeer.getBeerName());
        });
        

    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testUpdateExistingBeerButNotFound() {
        assertThrows(NotfoundException.class, () -> {
            beerController.editBeer(BeerDTO.builder().build(), UUID.randomUUID());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testPatchBeer() {
        Beer givenBeer = beerRepository.findAll().getFirst();
        BeerDTO beerDTO = beerMapper.beerToBeerDto(givenBeer);
        beerDTO.setBeerName("Well");

        BeerDTO editedBeerDTO = beerController.patchBeer(beerDTO, beerDTO.getId()).getBody();

        assertAll(() -> {
            assertNotNull(editedBeerDTO);
            assertEquals("Well", editedBeerDTO.getBeerName());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testPatchBeerDoesNotExist() {
        BeerDTO beerDTO = BeerDTO.builder().build();

        assertThrows(NotfoundException.class, () -> {
            beerController.patchBeer(beerDTO, UUID.randomUUID());
        });
    }

    @Test
    void testListBeers() {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers(null, null, null);
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(2413, beersDtos.size());
        });
    }

    @Test
    void testListBeerByName() throws Exception {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("IPA", null, null);
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(336, beersDtos.size()); 
        });
    }

    @Test
    void testListBeerByStyleAndBeerName() throws Exception {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("IPA", BeerStyle.IPA, null);
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(310, beersDtos.size());
        });
    }

    @Test
    void testListBeerNameWithShowInventory() throws Exception {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("Ninja Porter", null, true);
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(1, beersDtos.size());
            assertEquals("Ninja Porter", beersDtos.getFirst().getBeerName());
            assertEquals(140, beersDtos.getFirst().getQuantityOnHand());
        });
    }

    @Test
    void testListBeerNameWithoutShowInventory() throws Exception {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("Ninja Porter", null, false);
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(1, beersDtos.size());
            assertEquals("Ninja Porter", beersDtos.getFirst().getBeerName());
            assertNull(beersDtos.getFirst().getQuantityOnHand());
        });
    }

    @Test
    void testListBeerNameWithNullShowInventory() throws Exception {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers("Ninja Porter", null, null);
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(1, beersDtos.size());
            assertEquals("Ninja Porter", beersDtos.getFirst().getBeerName());
            assertNull(beersDtos.getFirst().getQuantityOnHand());
        });
    }

    @Test
    void testListBeerByStyle() throws Exception {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers(null, BeerStyle.IPA, null);
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(() -> {
            assert beersDtos != null;
            assertEquals(548, beersDtos.size());
        });
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testEmtpyListBeer() {
        beerRepository.deleteAll();
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers(null, null, null);
        List<BeerDTO> beerDtos = beersDtoResponseEntity.getBody();

        assertAll(
                () -> {
                    assert beerDtos != null;
                    assertEquals(0, beerDtos.size());
                }
        );
    }

    @Test
    void testGetBeerById() {
        UUID givenBeerId = beerRepository.findAll().getFirst().getId();

        ResponseEntity<BeerDTO> beerDTOResponseEntity = beerController.getBeerById(givenBeerId);
        BeerDTO beerDTO = beerDTOResponseEntity.getBody();

        assertAll(
                () -> {
                    assert beerDTO != null;
                    assertEquals(givenBeerId, beerDTO.getId());
                }
        );
    }

    @Test
    void testGetBeerByIdNotFound() {
        assertThrows(NotfoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }
}
