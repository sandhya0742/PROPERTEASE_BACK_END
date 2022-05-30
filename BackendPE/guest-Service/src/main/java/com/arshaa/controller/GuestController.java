package com.arshaa.controller;

import com.arshaa.common.Bed;
import com.arshaa.common.GuestModel;
import com.arshaa.dtos.GuestDto;

import com.arshaa.entity.Guest;
import com.arshaa.entity.GuestProfile;
import com.arshaa.entity.SecurityDeposit;
import com.arshaa.model.GuestsInNotice;
import com.arshaa.model.ResponseFile;
import com.arshaa.model.ResponseMessage;
import com.arshaa.model.VacatedGuests;
import com.arshaa.repository.GuestRepository;
import com.arshaa.service.GuestInterface;
import com.arshaa.service.GuestProfileService;
import com.arshaa.service.SecurityDepositService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/guest")
public class GuestController {

	@Autowired(required = true)
    private GuestRepository repository;

    @Autowired(required = true)
    private GuestInterface service;
    @Autowired
    private GuestProfileService gpServe;
    @Autowired
    private SecurityDepositService securityDepositService;
    @GetMapping("/getAllGuests")
    public List<GuestDto> getAllGuests() {
        return service.getGuests();
    }

    @PostMapping("/addGuest")
    public Guest saveGuest(@RequestBody Guest guest) {

        return service.addGuest(guest);

    }

    @GetMapping("/getGuestByGuestId/{id}")
    public Guest getOneGuest(@PathVariable("id") String id) {
        return service.getGuestById(id);
    }

    
    
    @DeleteMapping("/deleteGuestByGuestId/{id}")
    public void delete(@PathVariable("id") String id) {
        service.deleteGuest(id);
    }

    @PutMapping("/updateDueAmount")
    public double updateDueAmount(@RequestBody Guest guest) {
        return service.updateGuest(guest);
    }
  //http://localhost:7000/guest/findDueAmount/{guestId}
   	//FETCHING DUE AMOUNT BASED ON GUESTID .
   	@GetMapping("/findDueAmount/{guestId}")
   	public List<Guest> getByGuestId(@PathVariable String guestId) {
   		return service.getByGuestId(guestId);

   	}
     
   	  //http://localhost:7000/guest/getDueAmountOnDashBoard.
   	//FETCHING OverAllDUE AMOUNT. .
   	@GetMapping("/getDueAmountOnDashBoard")
   	public List<Guest> getTotalDue() {
   		return service.getTotalDue();

   	}

   	@GetMapping("/getGuestByBedId/{guestStatus}/{bedId}")
   	public ResponseEntity<GuestModel> getGuestByBedIdAndGuestStatus(@PathVariable String guestStatus, String bedId)
   	{
   		GuestModel gm=new GuestModel();
   		try {
   			Guest guest=repository.getGuestBybedIdAndGuestStatus(bedId,guestStatus);
   	   		if(guest.isGuestStatus().equalsIgnoreCase("active") ||guest.isGuestStatus().equalsIgnoreCase("inNotice") )
   	   		{
   	   			gm.setFirstName(guest.getFirstName());
   	   			gm.setId(guest.getId());
   	   			return new ResponseEntity(guest, HttpStatus.OK);
   	   		}
   				return new ResponseEntity("Guest is Inactive", HttpStatus.OK);
   		}
   		catch (Exception e) {
			// TODO: handle exception
				return new ResponseEntity(e.getMessage(), HttpStatus.OK);

		}
   		   	}
  
   	@GetMapping("/getPendingAndCompletedById/{buildingId}")
   	public List<Guest> getPendingByBuildingId(@PathVariable int buildingId) {
   	return service.getPendingByBuildingId(buildingId);
   	}
   	
//   	@GetMapping("/getFinalDueAmountForCheckout/{id}")
//   	public List<Guest> getCheckOutAmountByGuestId(@PathVariable String id){
//   	return service.getCheckOutAmountByGuestId(id);
//   	}
   	
   	@GetMapping("/get/{id}")
   	public List<Guest> getCheckOutDate(@PathVariable String id){
   		return service.getCheckOutAmountByGuestId(id);
   	}
   	
   	@GetMapping("/getFinalCheckout/{id}")
	public List<Guest> finalCheckOutGuest(@PathVariable String id){
   	return service.getFinalDueAmountById(id);
   	}
   	
   	@GetMapping("/onClickDues/{id}")
   	public List<Guest> getOnlyDues(@PathVariable String id){
   		return service.getOnlyDues(id);
   	}
   	@GetMapping("/findGuestsAreInNotice/{guestStatus}")
   	public List<GuestsInNotice> findByBuildingIdAndGuestStatus(@PathVariable String guestStatus)
   	{

		return service.findByBuildingIdAndGuestStatus(guestStatus);
   	}

   	@GetMapping("/findGuestAreVacated/{guestStatus}")
   	public List<VacatedGuests> findByGuestStatus(@PathVariable String guestStatus)
   	{

		return service.findByGuestStatus(guestStatus);
   	}
   	
   	@GetMapping("/getTotalPaid/{id}")
   	public List<Guest> getTotalPaidByGuestId(@PathVariable String id){
   	return this.service.getTotalPaidByGuestId(id);
   	}
   	
   	@SuppressWarnings({ "unchecked", "rawtypes" })
   	@GetMapping("/getBedIdByGuestId/{id}")
   	public ResponseEntity geeGuestBedByGuestId(@PathVariable String id) {
   	Guest guest= repository.getBedIdById(id);
   	return new ResponseEntity(guest.getBedId(),HttpStatus.OK);
   	}
   	
   	
   	@SuppressWarnings({ "unchecked", "rawtypes" })
   	@GetMapping("/getPhoneNumberByGuestId/{id}")
   	public ResponseEntity getPersonalNumberById(@PathVariable String id) {
   	Guest guest = repository.getPersonalNumberById(id);
   	return new ResponseEntity(guest.getPersonalNumber() , HttpStatus.OK);
   	}
   	
   	@SuppressWarnings("unchecked")
   	@GetMapping("/getNameByGuestId/{id}")
   	public ResponseEntity getGuestNumberById(@PathVariable String id) {
   	Guest guest = repository.getNameById(id);
   	return new ResponseEntity(guest.getFirstName().concat(" ").concat(guest.getLastName()) ,HttpStatus.OK );
   	}
   	
   	@GetMapping("/guestReport")
   	public List<Guest> getAllGuest(){
		return this.service.getAllGuest();   		   		
   		
   	}
   	
   //GuestProfile API's

   	@PostMapping("/upload/{guestId}")
	  public ResponseEntity<ResponseMessage> uploadFile(@PathVariable String guestId,@RequestParam("file") MultipartFile file) {
	    String message = "";
	    try {
	    	gpServe.store(file,guestId);
	      message = "Uploaded the file successfully: " + file.getOriginalFilename();
	      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
	    } catch (Exception e) {
	      message = "Can't able to upload file"+file.getOriginalFilename();
	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
	    }
	  }
	  @GetMapping("/files")
	  public ResponseEntity<List<ResponseFile>> getListFiles() {
	    List<ResponseFile> files = gpServe.getAllFiles().map(dbFile -> {
	      String fileDownloadUri = ServletUriComponentsBuilder
	          .fromCurrentContextPath()
	          .path("/files/")
	          .path(dbFile.getGuestId())
	          .toUriString();
	      return new ResponseFile(
	          dbFile.getName(),
	          fileDownloadUri,
	          dbFile.getType(),
	          dbFile.getData().length);
	    }).collect(Collectors.toList());
	    return ResponseEntity.status(HttpStatus.OK).body(files);
	  }
	  
	  
//	  @GetMapping("/files/{id}")
//	  public ResponseEntity<byte[]> getFile(@PathVariable String id) {
//		  UploadFile fileDB = storageService.getFile(id);
//	    return ResponseEntity.ok()
//	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
//	        .body(fileDB.getData);
//	  }
	  
	  @GetMapping("/files/{guestId}")
	  public ResponseEntity<ResponseFile> getFilebyID(@PathVariable String guestId) {
		  try {
			  GuestProfile fileDB = gpServe.getFileByID(guestId);
			  String fileDownloadUri = ServletUriComponentsBuilder
			          .fromCurrentContextPath()
			          .path("/files/")
			          .path(fileDB.getGuestId())
			          .toUriString();
			  ResponseFile file=new ResponseFile();
			  file.setUrl(fileDownloadUri);
			  file.setName(fileDB.getName());
			  file.setType(fileDB.getType());
			  file.setSize(fileDB.getData().length);
		    return new ResponseEntity<ResponseFile>(file,HttpStatus.OK);
  
		  }
		  catch(Exception e)
		  {
			    return new ResponseEntity("Something went wrong",HttpStatus.OK);
		  }
		  	  }
//	        
//	  }
//	private ResponseEntity<byte[]> ResponseEntity(byte[] bs, HttpStatus ok) {
//		// TODO Auto-generated method stub
//		return new ResponseEntity(bs,HttpStatus.OK);
//	}
//
	  
//	  @GetMapping("/filesData")
//	    public ResponseEntity<String[]> getListofFiles() {
//	    	UploadFile f=new UploadFile();
//	    	f.setName(FileUtil.folderPath);
//	    	
//	        return new  ResponseEntity(f,HttpStatus.OK);
//	  
//	    }
	  
	  
	  // SecurityDeposit API's
	  @PostMapping("/addSecurityDeposit")
		public ResponseEntity<SecurityDeposit> addData(@RequestBody SecurityDeposit sdepo){
		  
		  return securityDepositService.addData(sdepo);
			
		}
	  @GetMapping("/getSecurityDeposit")
		public ResponseEntity<List<SecurityDeposit>> getData(){
			return securityDepositService.getData();
		}
	  @PutMapping("/updateSecurityDeposit/{id}")
		public ResponseEntity  updateDataById(@PathVariable int id,@RequestBody SecurityDeposit sdepo) {
			return securityDepositService.updateDataById(id, sdepo);
		}
	  @DeleteMapping("deleteSecurityDeposit/{id}")
		public ResponseEntity  deleteDataById(@PathVariable int id) {
		  return securityDepositService.deleteDataById(id);
	  }
	  
	  //Get Security Deposit By Occupency Type API
	  @GetMapping("/getSecurityDepositByOccupencyType/{occupencyType}")
	  public ResponseEntity getSecurityDepositByOccupencyType(@PathVariable String occupencyType) {
		  return securityDepositService.getSecurityDepositByOccupencyType(occupencyType);
		  
	  }
	  

	}





