package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.model.GenericApiResponse;
import com.wevolv.unionservice.model.Member;
import com.wevolv.unionservice.model.MemberInvite;
import com.wevolv.unionservice.model.dto.MemberDto;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.ObjectDeletedResponse;
import com.wevolv.unionservice.service.MemberInviteService;
import com.wevolv.unionservice.service.MemberService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;
import java.util.Optional;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final MemberInviteService memberInviteService;

    public MemberController(MemberService memberService, MemberInviteService memberInviteService) {
        this.memberService = memberService;
        this.memberInviteService=memberInviteService;
    }

    @PostMapping(value = "/create")
    public ObjectCreatedResponse createNewMember(HttpServletRequest request, @RequestBody MemberDto memberDto) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var member = memberService.createNewMember(memberDto);
        return new ObjectCreatedResponse(member.getId());
    }

    @DeleteMapping(value = "/delete/{memberId}")
    public ObjectDeletedResponse deleteMember(HttpServletRequest request, @PathVariable String memberId) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        memberService.deleteMember(memberId);
        return new ObjectDeletedResponse(true);

    }

    @PostMapping(value = "/update/{memberId}")
    public Member updateMember(HttpServletRequest request, @RequestBody MemberDto memberDto, @PathVariable String memberId) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return memberService.updateMember(memberDto, memberId);
    }

    @GetMapping(value = "/all")
    public Map<String, Object> getAllMembers(HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return memberService.getAllMembers(paging);

    }

    @GetMapping(value = "/{memberId}")
    public ResponseEntity<GenericApiResponse> getMemberById(HttpServletRequest request, @PathVariable String memberId) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var member = memberService.getMemberById(memberId);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(member)
                .message(String.format("Member with this memberId %s is here!", memberId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search/{word}")
    public Map<String, Object> searchMembers(@PathVariable String word,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        var paging = PageRequest.of(page, size);
        return memberService.search(word, paging);
    }

    @GetMapping("/get/representatives")
    public Map<String, Object> getRepresentativeMembers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        var paging = PageRequest.of(page, size);
        return memberService.getRepresentativeMembers(paging);
    }

    @GetMapping("/get/players")
    public Map<String, Object> getPlayerMembers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        var paging = PageRequest.of(page, size);
        return memberService.getPlayerMembers(paging);
    }

    @PutMapping("/invite/{role}/{email}")
    public ResponseEntity<MemberInvite> inviteMember(HttpServletRequest request, @PathVariable String role, @PathVariable String email) {

        String jwt = request.getHeader("Authorization");
        String keyCloakId = getUserIdFromToken(jwt);

        return ResponseEntity.ok(memberInviteService.invite(email, role, keyCloakId));
    }
    
     @GetMapping("/invite/{inviteId}")
    public ResponseEntity<GenericApiResponse> getInvite(@PathVariable String inviteId) {
            
      Optional<MemberInvite> meberInvite  = memberInviteService.getInviteDetail(inviteId);
       if(meberInvite.isPresent()){
            return ResponseEntity.accepted().body(GenericApiResponse.builder().message("success").response(meberInvite.get()).statusCode(HttpStatus.ACCEPTED.value()).build());
       }else{
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericApiResponse.builder().message("Invalid InviteId").statusCode(HttpStatus.NOT_FOUND.value()).build());
       }

    }

    
     @PutMapping("/invite/accept/{inviteId}")
    public ResponseEntity<GenericApiResponse> acceptInvite(@PathVariable String inviteId) {
            
       boolean accepted= memberInviteService.accept(inviteId);
       if(accepted){
            return ResponseEntity.accepted().body(GenericApiResponse.builder().message("Invite Accepted").statusCode(HttpStatus.ACCEPTED.value()).build());
       }else{
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericApiResponse.builder().message("Invalid InviteId").statusCode(HttpStatus.NOT_FOUND.value()).build());
       }

    }

}
