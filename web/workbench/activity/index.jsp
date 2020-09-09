<%@ page language="java" import="java.util.*" pageEncoding="utf-8" contentType="text/html; utf-8" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">
	$(function(){
		// 1.为创建按钮绑定事件，打开添加操作的模态窗口
		$("#addBtn").click(function(){
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});

			/*
			操作模态窗口的方式：
				找到需要操作的模态窗口的jQuery对象，调用modal方法，为该方法传递参数 show：打开模态窗口 hide:关闭模态窗口
			 */
			// 弹出模态窗口前，应先拿到后台数据，为所有者下拉框铺值
			const $dom = $("#create-marketActivityOwner");
			$.ajax({
				url:"workbench/activity/getUserList.do",
				data:{},
				type:"get",
				dataType:"json",
				success:function (data){
					/*
					data:userList(json数组)
					 */
					// empty()将数组中所有dom对象的子对象删除
					$dom.empty(); // 添加前要删除旧的数据，防止多次添加产生多余重复的数据
					$.each(data,function(index,element){
						// append() 将数组中所有dom对象添加子对象
						$dom.append("<option value='"+element.id+"'>"+element.name+"</option>");
					});
					// 取得当前登录用户的id
					// 在js中使用el表达式，el表达式一定要套用在字符串中
					const id = "${sessionScope.user.id}"; // 作用域对象别名.共享数据.属性名
					$dom.val(id);
				}
			});
			// 将所有下拉框处理完后，就展现模态窗口
			$("#createActivityModal").modal("show");
		});

		// 为保存按钮绑定事件，并执行添加操作
		$("#saveBtn").click(function(){
			$.ajax({
				url: "workbench/activity/save.do",
				type: "post",
				data: {
					"owner": $.trim($("#create-marketActivityOwner").val()),
					"name": $.trim($("#create-marketActivityName").val()),
					"startDate": $.trim($("#create-startTime").val()),
					"endDate": $.trim($("#create-endTime").val()),
					"cost": $.trim($("#create-cost").val()), // 成本
					"description": $.trim($("#create-describe").val()) // 描述
				},
				dataType: "json",
				success: function(data){
					/*
					data:{success:true/false}
					 */
					if(data.success){
						alert("添加市场活动成功");
						// 1.刷新市场活动信息列表(局部刷新)
						// pageList(1,2);
						/*
						参数1：$("#activityPage").bs_pagination('getOption', 'currentPage')
							操作后停留在当前页
						参数2：$("#activityPage").bs_pagination('getOption', 'rowsPerPage')
							操作后维持已经设置好的每页展现的记录数
						*/
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						// 做完添加操作后，应该回到第一页，维持每页展现的记录数

						// 2.清空添加模态窗口中的数据
						/*$("#create-marketActivityOwner").val("");
						$("#create-marketActivityName").val("");
						$("#create-startTime").val("");
						$("#create-endTime").val("");
						$("#create-cost").val("");
						$("#create-describe").val("");*/
						// 提交表单
						// $("#activityAddForm").submit();
						/*
							$("#activityAddForm").reset();
							注意：我们拿到了form表单的jQuery对象，对于表单的jQuery对象，提供了submit()方法让我们提交表单
							但是表单的jQuery对象，没有为我们提供reset()方法让我们重置表单
							虽然jQuery对象没有为我们提供reset方法，但是原生js为我们提供了reset方法
						*/
						// jquery对象转为dom对象
						$("#activityAddForm").get(0).reset();
						// 3.关闭添加操作的模态窗口
						$("#createActivityModal").modal("hide");
					}else{
						alert("添加市场活动失败");
					}
				}
			});
		});

		// 页面加载完毕之后，触发一个方法
		pageList(1,2); // 默认展开列表第一页，每页展现两条记录

		// 为查询绑定按钮，触发pageList方法
		$("#searchBtn").click(function(){
			/*
			点击查询按钮的时候，我们应该将搜索框中的信息保存起来，保存到隐藏域中
			 */
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));
			pageList(1,2);
		});

		// 为全选的复选框绑定事件，触发全选操作
		$("#completeSelection").click(function(){
			$("input[name=xz]").prop("checked",this.checked);
		});
		/*
		以下这种做法是不行的
		$("input[name=xz]").click(function(){
		})
		因为动态生成的元素，是不能够以普通绑定事件的形式来进行操作的
		*/
		/*
		动态生成的元素，我们要以on方法的形式来触发事件
		语法：
			$(需要绑定元素的有效的外层元素).on(绑定事件的方式,需要绑定的元素的jquery对象,回调函数)
		 */

		$("#activityBody").on("click",$("input[name=xz]"),function(){
			$("#completeSelection").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
		});

		// 为删除按钮绑定事件，执行市场活动删除操作
		$("#deleteBtn").click(function(){
			// 找到所有选中市场活动的复选框的jquery对象
			var $xz = $("input[name=xz]:checked");
			if($xz.length == 0){
				alert("请选择需要删除的市场活动！");
			} else{
				if(confirm("确定删除所选中的记录吗？")){
					// 删除过程中，有可能选了一个或者多个
					// url:workbench/activity/delete.do?id=xxx&id=xxx&id=xxx
					// 拼接参数
					var param = "";
					// 将$xz中的每一个dom对象遍历出来，取其value值，就相当于取得了需要删除的记录的id
					for(var i = 0;i < $xz.length;i++){
						param += "id="+$($xz[i]).val();
						// 如果不是最后一个元素，需要在后面追加一个&符
						if(i < $xz.length-1){
							param += "&";
						}
					}
					// alert(param);
					$.ajax({
						url: "workbench/activity/delete.do",
						type: "post",
						data: param,
						dataType: "json",
						success: function(data){
							/*
                            data:{"success",true/false}
                             */
							if(data.success){
								// 删除成功后，展现第一页，维持每页所展现的记录数
								// pageList(1,2);
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							}else{
								alert("删除失败！")
							}
						}
					});
				}
			}
		});

		// 为修改按钮绑定事件，打开修改操作的模态窗口
		$("#editBtn").click(function(){
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			// 获取选中的市场活动的id
			var $xz = $("input[name=xz]:checked");
			if($xz.length == 0){
				alert("请选择需要修改的市场活动！");
			} else if($xz.length > 1){
				alert("只能选择其中一个，进行修改哦！")
			} else{
				// 肯定只选中了其中一个
				var id = $xz.val();
				$.ajax({
					url: "workbench/activity/getUserListAndActivity.do",
					type: "get",
					data: {"id": id},
					dataType: "json",
					success: function(data){
						/*
						data:用户列表和市场活动对象
						{"userList":[{u1},{u2}],"activity":{市场活动}}
						 */
						// 1.处理所有者的下拉框
						var html = "";
						$.each(data.userList,function(index,element){
							html += "<option value='" + element.id + "'>" + element.name + "</option>"
						});
						// alert(html);
						$("#edit-marketActivityOwner").html(html);

						// 2.处理单条activity
						$("#edit-id").val(data.activity.id);
						$("#edit-marketActivityName").val(data.activity.name);
						$("#edit-marketActivityOwner").val(data.activity.owner);
						$("#edit-startTime").val(data.activity.startDate);
						$("#edit-endTime").val(data.activity.endDate);
						$("#edit-cost").val(data.activity.cost);
						$("#edit-describe").val(data.activity.description);

						// 打开修改操作的模态窗口
						$("#editActivityModal").modal("show");
					}
				})
			}
		});

		// 为更新按钮绑定事件，执行市场活动的修改操作
		/*
		在实际项目开发中，一定是按照先做添加，再做修改的这种顺序，所以，为了节省开发时间，修改操作一般都是copy添加操作
		 */
		$("#updateBtn").click(function(){
			$.ajax({
				url: "workbench/activity/update.do",
				type:"post",
				data: {
					"id": $.trim($("#edit-id").val()),
					"owner": $.trim($("#edit-marketActivityOwner").val()),
					"name": $.trim($("#edit-marketActivityName").val()),
					"startDate": $.trim($("#edit-startTime").val()),
					"endDate": $.trim($("#edit-endTime").val()),
					"cost": $.trim($("#edit-cost").val()), // 成本
					"description": $.trim($("#edit-describe").val()) // 描述
				},
				dataType: "json",
				success: function(data){
					/*
					data:{"success":true/false}
					 */
					if (data.success){
						alert("记录修改成功");
						// pageList(1,2);
						/*
							修改操作后，应该维持在当前页，维持每页展现的记录数
						 */
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						// $("#activityAddForm").get(0).reset();
						// 3.关闭修改操作的模态窗口
						$("#editActivityModal").modal("hide");
					} else{
						alert("修改市场活动失败");
					}
				}
			})
		})
	});
	/*
	对于所有的关系型数据库，做前端的分页相关操作的基础组件就是pageNo和pageSize
	pageNo:页码
	pageSize:每页展现的记录数
	pageList方法：就是发出ajax请求到后台，从后台取得最新的市场活动信息列表数据，通过响应回来的数据，局部刷新市场活动信息列表
	我们都在哪些情况下，需要调用pageList方法(什么情况下需要刷新一下市场活动列表)
	(1)点击左侧"市场活动"超链接
	(2)添加，修改，删除后，需要刷新市场活动列表
	(3)点击查询按钮的时候，需要刷新市场活动列表
	(4)点击分页组件的时候
	以上为pageList方法制定了6个入口，也就是说，在以上6个操作执行完毕后，我们必须 调用pageList方法，刷新市场活动信息列表
	 */
	function pageList(pageNo,pageSize){
		// 将复选框中选中的市场活动，去掉
		$("#completeSelection").prop("checked",false);
		// 查询前，将隐藏域中保存的信息取出来，重新赋予到搜索框中
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));
		$.ajax({
			url: "workbench/activity/pageList.do",
			type: "get",
			data: {
				"pageNo": pageNo,
				"pageSize": pageSize,
				"name" : $.trim($("#search-name").val()),
				"owner" : $.trim($("#search-owner").val()),
				"startDate" : $.trim($("#search-startDate").val()),
				"endDate" : $.trim($("#search-endDate").val())
			},
			dataType: "json",
			success: function(data){
				/*
				data:
					市场活动信息列表：json数组([{市场活动1,市场活动2,}])
					分页插件需要的是查询出来的总记录数：{"total":xxx}
					拼接即可
					[{"total":xxx},{"dataList":{市场活动1,市场活动2,}}]
				 */
				var html = "";
				// 每一个element就是一个市场活动对象
				$.each(data.dataList,function(index,element){
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value='+ element.id +'></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+element.id+'\';">'+ element.name +'</a></td>';
					html += '<td>'+ element.owner +'</td>';
					html += '<td>'+ element.startDate+'</td>';
					html += '<td>'+ element.endDate +'</td>';
					html += '</tr>';
				});
				$("#activityBody").html(html);
				// 数据处理完毕后，结合分页查询(插件)，对前端展现分页信息
				// 计算总页数
				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数
					visiblePageLinks: 3, // 显示几个卡片
					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,
					// 该回调函数是在，点击分页组件的时候触发
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});

			}
		});
	}

</script>
</head>
<body>
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>
	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<!--
						data-dismiss="modal":表示关闭模态窗口
					-->
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id"/>
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
								  <!--
								  <option>zhangsan</option>
								  <option>lisi</option>
								  <option>wangwu</option>
								  -->
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startTime">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endTime">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<!--
									关于文本域textarea:
										(1)一定是要以标签对的形式来呈现，正常状态下标签对要紧紧的挨着
										(2)textarea虽然是以标签对的形式来呈现的，但是它也是属于表单元素范畴
										我们所有的对于textarea的取值和赋值操作，应该统一使用val()方法(而不是html()方法)
								-->
								<textarea class="form-control" rows="3" id="edit-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>

	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate">
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<!--
						点击创建按钮，观察两个属性和属性值
						data-toggle="modal"：表示触发该按钮，将要打开一个莫态窗口
						data-target="#createActivityModal"：表示要打开哪个模态窗口，通过#id的形式找到该窗口

						现在我们是以属性和属性值的方式写在了button元素中，用来打开模态窗口
						但是这样做事有问题的：
							问题在于没有办法对按钮的功能进行扩充
						所以未来的实际项目开发中，对于触发模态窗口的操作，一定不要写死在元素当中，应该由我们自己写js代码来操作
					-->
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="completeSelection"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
					<!--
						<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>
					-->
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage">

				</div>
			</div>
			
		</div>
	</div>
</body>
</html>