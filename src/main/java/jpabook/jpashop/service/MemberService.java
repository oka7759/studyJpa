package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        vailidateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void vailidateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원");

        }
    }

    /**
     * 전체조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }


    /**
     * 한명조회
     */
    public Member findMember(Long id) {
        return memberRepository.findOne(id);
    }
}
