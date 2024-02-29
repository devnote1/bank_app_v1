<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/layout/header.jsp"%>

<div class="col-sm-8">
	<h2>회원 가입</h2>
	<h5>Bank App 오신 걸 환영합니다.</h5>
	<!-- multipart/form-data 반드시 선언 -->
	<form action="/user/sign-up" method="post" enctype="multipart/form-data">
		<div class="form-group">
			<label for="username">Username:</label>
			<input type="text" class="form-control" placeholder="Enter username" id="username" name="username" value="cos">
		</div>
		<div class="form-group">
			<label for="pwd">Password: </label>
			<input type="password" class="form-control" placeholder="Enter password" id="pwd" name="password" value="1234">
		</div>
		<div class="form-group">
			<label for="fullname">Fullname: </label>
			<input type="text" class="form-control" placeholder="Enter fullname" id="fullname" name="fullname" value="spring">
		</div>
		<!-- 코드 추가 -->		
		<div class="form-group custom-file">
			<input type="file" class="custom-file-input" id="customFile" name="mFile">
			<label class="custom-file-label" for="customFile">Choose file</label>
		</div>
		<br><br>
		<div class="text-right">
			<button type="submit" class="btn btn-primary">회원가입</button>
		</div>
	</form>
</div>
</div>
</div>

<!-- 코드 추가 -->
<script>
$(".custom-file-input").on("change", function() {
  let fileName = $(this).val().split("\\").pop();
  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
});
</script>

<%@ include file="/WEB-INF/view/layout/footer.jsp"%>


