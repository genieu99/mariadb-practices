package bookmall.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bookmall.vo.CartVo;
import bookmall.vo.CategoryVo;

public class CartDao {
	
	private Connection getConnection() throws SQLException {
		Connection conn = null;
		
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			
			String url = "jdbc:mariadb://192.168.64.3:3306/bookmall?charset=utf8";
			conn = DriverManager.getConnection(url, "bookmall", "bookmall");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패: " + e);
		}
		
		return conn;
	}

	public void insert(CartVo mockCartVo) {
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("insert into cart(user_no, book_no, quantity) values(?, ?, ?)");
		) {
			pstmt.setLong(1, mockCartVo.getUserNo());
			pstmt.setLong(2, mockCartVo.getBookNo());
			pstmt.setLong(3, mockCartVo.getQuantity());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
	}

	public List<CartVo> findByUserNo(Long userNo) {
		List<CartVo> result = new ArrayList<>();
		
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("select book_no, user_no, quantity, title from cart, book where cart.book_no = book.no and user_no = ?");
		) {
			pstmt.setLong(1, userNo);
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {	
				Long book_no = rs.getLong(1);
				Long user_no = rs.getLong(2);
				int quantity = rs.getInt(3);
				String title = rs.getString(4);
				
				CartVo cartVo = new CartVo();
				cartVo.setBookNo(book_no);
				cartVo.setUserNo(user_no);
				cartVo.setQuantity(quantity);
				cartVo.setBookTitle(title);
				
				result.add(cartVo);
			}
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
		
		return result;
	}
	
	public void deleteByUserNoAndBookNo(Long userNo, Long bookNo) {
		try (
				Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement("delete from cart where user_no = ? and book_no = ?");
		) {
			pstmt.setLong(1, userNo);
			pstmt.setLong(2, bookNo);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error: " + e);
		}
	}

}
