package jsl.moum.chatroom.dto;

import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomMemberInfoDto {

    private int id;
    private String name;
    private String username;
    private String profileImageUrl;

    public ChatroomMemberInfoDto(MemberEntity memberEntity){
        this.id = memberEntity.getId();
        this.name = memberEntity.getName();
        this.username = memberEntity.getUsername();
        this.profileImageUrl = memberEntity.getProfileImageUrl();
    }

}
