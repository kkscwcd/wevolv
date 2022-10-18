package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.model.MemberInvite;
import com.wevolv.unionservice.repository.MemberInviteRepository;
import com.wevolv.unionservice.service.MemberInviteService;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MemberInviteServiceImpl implements MemberInviteService {

    private MemberInviteRepository memberInviteRepository;

    @Override
    public MemberInvite invite(String email, String role, String invitedBy) {

        MemberInvite memberInvite = MemberInvite.builder()
                .id(UUID.randomUUID().toString())
                .accepted(Boolean.FALSE)
                .email(email)
                .role(role)
                .invitedBy(invitedBy)
                .inviteDate(new Date()).build();

        return memberInviteRepository.save(memberInvite);
    }

    @Override
    public boolean accept(String inviteId) {

        Optional<MemberInvite> memberInvite = memberInviteRepository.findById(inviteId);
        // Check invite id valid and Invite not accepted
        if (memberInvite.isPresent() && !memberInvite.get().getAccepted()) {
            MemberInvite m = memberInvite.get();
            m.setAccepted(Boolean.TRUE);
            m.setAcceptedDate(new Date());
            memberInviteRepository.save(m);
            
            //TODO: Make a call to send invite accpeted confirmation email/SMS
            

            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<MemberInvite> getInviteDetail(String inviteId) {
        return memberInviteRepository.findById(inviteId);
    }

}
