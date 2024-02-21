<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- header.jsp -->
<%@ include file="/WEB-INF/view/layout/header.jsp"%>

<!-- start of content.jsp(메인 영역)   -->
<div class="col-sm-8">
	<h2>회원 가입</h2>
	<h5>Bank App 오신 걸 환영합니다.</h5>
	<form action="/user/sign-up" method="post">
		<div class="form-group">
			<label for="username">Username:</label>
			<input type="text" class="form-control" placeholder="Enter username" id="username" name="username" value="cos">
		</div>
		<div class="form-group">
			<label for="pwd">Password: </label>
			<input type="password" class="form-control" placeholder="Enter password" id="pwd" name="password" value="asd1234">
		</div>
		<div class="form-group">
			<label for="fullname">Fullname: </label>
			<input type="text" class="form-control" placeholder="Enter fullname" id="fullname" name="fullname" value="spring">
		</div>
		<div class="text-right">
			<button type="submit" class="btn btn-primary">회원가입</button>
		</div>
	</form>
</div>
</div>
</div>

<!-- end of content.jsp(메인 영역)   -->

<!-- footer.jsp -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>


