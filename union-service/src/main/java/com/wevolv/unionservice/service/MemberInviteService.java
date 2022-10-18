package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.MemberInvite;
import java.util.Optional;

public interface MemberInviteService {
   
    MemberInvite invite(String email,String role,String invitedBy);

    boolean accept(String inviteId);
    
     Optional<MemberInvite> getInviteDetail(String inviteId);

}
