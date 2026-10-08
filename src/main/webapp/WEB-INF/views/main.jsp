<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>메인 대시보드 - 교보 웹 서비스</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/todo.css">
    <style>
        /* 대시보드 전용 확장 스타일 */
        .dashboard-container {
            max-width: 680px;
        }
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 16px;
            margin-top: 24px;
        }
        .menu-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            padding: 20px;
            text-decoration: none;
            color: inherit;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: all 0.25s ease-in-out;
            box-shadow: 0 2px 4px rgba(0,0,0,0.04);
        }
        .menu-card:hover {
            transform: translateY(-4px);
            border-color: #3182ce;
            box-shadow: 0 8px 16px rgba(49, 130, 206, 0.15);
        }
        .menu-card.disabled {
            opacity: 0.6;
            cursor: not-allowed;
            background: #f8fafc;
        }
        .menu-card.disabled:hover {
            transform: none;
            border-color: #e2e8f0;
            box-shadow: none;
        }
        .menu-icon {
            font-size: 32px;
            margin-bottom: 12px;
        }
        .menu-title {
            font-size: 17px;
            font-weight: 700;
            color: #2d3748;
            margin-bottom: 6px;
        }
        .menu-desc {
            font-size: 13px;
            color: #718096;
            line-height: 1.4;
            margin-bottom: 16px;
        }
        .menu-badge {
            align-self: flex-start;
            font-size: 11px;
            font-weight: 600;
            padding: 3px 8px;
            border-radius: 9999px;
            background: #ebf8ff;
            color: #3182ce;
        }
        .menu-badge.badge-gray {
            background: #edf2f7;
            color: #718096;
        }
    </style>
</head>
<body>

<div class="app-card dashboard-container">
    <!-- 1. 상단 사용자 정보 및 로그아웃 바 -->
    <div class="user-bar">
        <div class="user-badge">
            👤 <strong>${sessionScope.loginUser.name}</strong> (${sessionScope.loginUser.userId})님 환영합니다!
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">로그아웃</a>
    </div>

    <!-- 2. 대시보드 타이틀 헤더 -->
    <div class="app-header" style="text-align: left; margin-bottom: 16px;">
        <h1 class="app-title" style="font-size: 22px;">서비스 대시보드</h1>
        <p class="app-subtitle">이용하실 서비스를 아래에서 선택해주세요.</p>
    </div>

    <!-- 3. 서비스 선택 메뉴 그리드 영역 -->
    <div class="menu-grid">

        <!-- [기능 카드 1: TODO LIST 바로가기] -->
        <a href="${pageContext.request.contextPath}/todo" class="menu-card">
            <div>
                <div class="menu-icon">📝</div>
                <div class="menu-title">할 일 목록 (Todo List)</div>
                <div class="menu-desc">나만의 할 일을 등록하고, 완료 여부를 체크하며 일정을 관리합니다.</div>
            </div>
            <span class="menu-badge">이용 가능</span>
        </a>

        <!-- [기능 카드 2: 자유 게시판 바로가기 (확장용)] -->
        <a href="${pageContext.request.contextPath}/board" class="menu-card">
            <div>
                <div class="menu-icon">📋</div>
                <div class="menu-title">자유 게시판</div>
                <div class="menu-desc">동료들과 다양한 주제로 소통하고 게시글을 작성할 수 있습니다.</div>
            </div>
            <span class="menu-badge">준비 중</span>
        </a>

        <!-- [기능 카드 3: 마이페이지 (향후 확장 예정 예시)] -->
        <div class="menu-card disabled">
            <div>
                <div class="menu-icon">⚙️</div>
                <div class="menu-title">내 정보 관리</div>
                <div class="menu-desc">비밀번호 변경 및 개인 설정을 수정할 수 있는 페이지입니다.</div>
            </div>
            <span class="menu-badge badge-gray">준비 중</span>
        </div>

    </div>
</div>

</body>
</html>