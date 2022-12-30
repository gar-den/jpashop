package jpabook.jpashop.service;

import java.util.List;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)  // 조회 쿼리에 최적화 (dirty checking 안 함 등...)
@RequiredArgsConstructor // final 있는 애들만 생성자 만들어 줌 -> MemberService 생성자에 memberRepository injection 필요 X (생성자 injection)
public class MemberService {
	private final MemberRepository memberRepository;

	// 회원 가입
	@Transactional  // readOnly 가 default false -> 덮어 씌워짐
	public Long join(Member member) {
		validateDuplicatedMember(member);  // 중복 회원 검증
		memberRepository.save(member);

		return member.getId();
	}

	private void validateDuplicatedMember(Member member) {
		List<Member> members = memberRepository.findByName(member.getName());

		if (!members.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}

	// 회원 전체 조회
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}

	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}
}
