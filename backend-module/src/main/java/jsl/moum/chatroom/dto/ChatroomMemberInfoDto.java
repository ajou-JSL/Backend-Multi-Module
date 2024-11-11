package jsl.moum.chatroom.dto;

import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomMemberInfoDto {

    private int memberId;
    private String name;
    private String username;
    private String profileImageUrl;

    public ChatroomMemberInfoDto(MemberEntity memberEntity){
        this.memberId = memberEntity.getId();
        this.name = memberEntity.getName();
        this.username = memberEntity.getUsername();
        this.profileImageUrl = memberEntity.getProfileImageUrl();
    }

}
