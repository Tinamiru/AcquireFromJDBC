package kr.or.ddit.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import kr.or.ddit.util.JDBCUtil;

/*
	회원정보를 관리하는 프로그램을 작성하는데 
	아래의 메뉴를 모두 구현하시오. (CRUD기능 구현하기)
	(DB의 MYMEMBER테이블을 이용하여 작업한다.)
	
	* 자료 삭제는 회원ID를 입력 받아서 삭제한다.
	
	예시메뉴)
	----------------------
		== 작업 선택 ==
		1. 자료 입력			---> insert
		2. 자료 삭제			---> delete
		3. 자료 수정			---> update
		4. 전체 자료 출력	---> select
		5. 작업 끝.
	----------------------
	 
	   
// 회원관리 프로그램 테이블 생성 스크립트 
create table mymember(
    mem_id varchar2(8) not null,  -- 회원ID
    mem_name varchar2(100) not null, -- 이름
    mem_tel varchar2(50) not null, -- 전화번호
    mem_addr varchar2(128),    -- 주소
    reg_dt DATE DEFAULT sysdate, -- 등록일
    CONSTRAINT MYMEMBER_PK PRIMARY KEY (mem_id)
);

*/
public class T05MemberInfoTest {

	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;

	private Scanner scan = new Scanner(System.in);

	/**
	 * 메뉴를 출력하는 메서드
	 */
	public void displayMenu() {
		System.out.println();
		System.out.println("----------------------");
		System.out.println("  === 작 업 선 택 ===");
		System.out.println("  1. 자료 입력");
		System.out.println("  2. 자료 삭제");
		System.out.println("  3. 자료 수정");
		System.out.println("  4. 전체 자료 출력");
		System.out.println("  5. 작업 끝.");
		System.out.println("----------------------");
		System.out.print("원하는 작업 선택 >> ");
	}

	/**
	 * 프로그램 시작메서드
	 */
	public void start() {
		int choice;
		do {
			displayMenu(); // 메뉴 출력
			choice = scan.nextInt(); // 메뉴번호 입력받기
			switch (choice) {
			case 1: // 자료 입력
				insertMember();
				break;
			case 2: // 자료 삭제
				deleteMember();
				break;
			case 3: // 자료 수정
				updateMember();
				break;
			case 4: // 전체 자료 출력
				displayMemberAll();
				break;
			case 5: // 작업 끝
				System.out.println("작업을 마칩니다.");
				break;
			default:
				System.out.println("번호를 잘못 입력했습니다. 다시입력하세요");
			}
		} while (choice != 5);
	}

	private void deleteMember() {
		System.out.println();
		System.out.println("삭제할 회원 정보를 입력하세요.");
		System.out.print("회원 ID >> ");

		String memID = scan.next();

		try {

			conn = JDBCUtil.getConnection();
			String sql = "delete from mymember where mem_id = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, memID);

			int cnt = pstmt.executeUpdate();

			if (cnt > 0) {
				System.out.println(memID + " 회원정보 삭제 성공.");
			} else {
				System.out.println(memID + " 회원정보 삭제 실패.");
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			JDBCUtil.close(conn, stmt, pstmt, rs);
			;
		}

	}

	/**
	 * 회원 정보를 수정하기위한 메소드
	 */
	private void updateMember() {
		String memID = "";
		boolean isExist = false; // 중복 체크

		do {
			System.out.println();
			System.out.println("수정할 회원 정보를 입력하세요.");
			System.out.print("회원 ID >> ");

			memID = scan.next();

			isExist = checkMember(memID);

			if (!isExist) {
				System.out.println("회원 ID가 " + memID + "인 회원은 이미 존재하지 않습니다.");
				System.out.println("다시 입력해주세요");
			}

		} while (!isExist);

		System.out.print("회원 이름 >> ");
		String memName = scan.next();

		System.out.print("회원 전화번호 >> ");
		String memTel = scan.next();

		scan.nextLine(); // 입력버퍼 비우기

		System.out.print("회원 주소 >> ");
		String memAddr = scan.nextLine();
		try {

			conn = JDBCUtil.getConnection();
			String sql = "update mymember set mem_name = ?, mem_tel = ?, mem_addr = ? where mem_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memName);
			pstmt.setString(2, memTel);
			pstmt.setString(3, memAddr);
			pstmt.setString(4, memID);

			int cnt = pstmt.executeUpdate();

			if (cnt > 0) {
				System.out.println(memID + "회원의 정보를 수정했습니다.");
			} else {
				System.out.println(memID + "회원의 정보를 수정하지 못했습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, stmt, pstmt, rs);
		}
	}

	private void displayMemberAll() {
		try {

			conn = JDBCUtil.getConnection();

			System.out.println("----------------------------------------------------");
			System.out.println("ID\t이 름\t전화번호\t\t주   소");
			System.out.println("----------------------------------------------------");

			String sql = "select * from mymember";

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String memID = rs.getString("mem_id");
				String memName = rs.getString("mem_name");
				String memTel = rs.getString("mem_tel");
				String memAddr = rs.getString("mem_addr");

				System.out.println(memID + "\t" + memName + "\t" + memTel + "\t\t" + memAddr);

			}
			System.out.println("-------------------------------------------------------");
			System.out.println("출력작업 끝...");

		} catch (SQLException e) {
			System.out.println("회원자료 가져오기 실패!!!");
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, stmt, pstmt, rs);

		}
	}

	/**
	 * 회원정보 추가 메서드
	 */

	private void insertMember() {
		String memID = "";
		boolean isExist = false; // 중복 체크

		do {
			System.out.println();
			System.out.println("추가할 회원 정보를 입력하세요.");
			System.out.print("회원 ID >> ");

			memID = scan.next();

			isExist = checkMember(memID);

			if (isExist) {
				System.out.println("회원 ID가 " + memID + "인 회원은 이미 존재합니다.");
				System.out.println("다시 입력해주세요");
			}

		} while (isExist);

		System.out.print("회원 이름 >> ");
		String memName = scan.next();

		System.out.print("회원 전화번호 >> ");
		String memTel = scan.next();

		scan.nextLine(); // 입력버퍼 비우기

		System.out.print("회원 주소 >> ");
		String memAddr = scan.nextLine();

		try {
			conn = JDBCUtil.getConnection();

			String sql = "INSERT INTO mymember " + " ( mem_id, mem_name, mem_tel, mem_addr, reg_dt ) "
					+ " VALUES (?, ?, ?, ?, sysdate) ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memID);
			pstmt.setString(2, memName);
			pstmt.setString(3, memTel);
			pstmt.setString(4, memAddr);

			int cnt = pstmt.executeUpdate();

			if (cnt > 0) {
				System.out.println(memID + " 회원 추가 작업 성공!");
			} else {
				System.out.println(memID + " 회원 추가 작업 실패..");

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, stmt, pstmt, rs);
		}

	}

	/**
	 * 회원이 존재하는지 확인하기 위한 메서드
	 * 
	 * @param memID 회원ID
	 * @return 존재하면 true, 존재하지 않으면 false 반환
	 */

	private boolean checkMember(String memID) {
		boolean isExist = false;

		try {
			conn = JDBCUtil.getConnection();
			String sql = "select count(*) as cnt from mymember where mem_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memID);

			rs = pstmt.executeQuery();

			int cnt = 0;
			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}

			if (cnt > 0) {
				isExist = true;
			} else {
				isExist = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, stmt, pstmt, rs);
		}

		return isExist;
	}

	public static void main(String[] args) {
		T05MemberInfoTest memObj = new T05MemberInfoTest();
		memObj.start();
	}

}
