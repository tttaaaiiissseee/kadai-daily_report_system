package controllers.reports;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsCreateServlet
 */
@WebServlet("/reports/create")
public class ReportsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsCreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = (String)request.getParameter("_token");
        if(_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            Report r = new Report();

            r.setEmployee((Employee)request.getSession().getAttribute("login_employee"));

            Date report_date = new Date(System.currentTimeMillis());
            String rd_str = request.getParameter("report_date");
            if(rd_str != null && !rd_str.equals("")) {
                report_date = Date.valueOf(request.getParameter("report_date"));
            }
            r.setReport_date(report_date);


            java.util.Date date = new java.util.Date();
            String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            String attendance = request.getParameter("attendance_time");
            String str = strDate + " " + attendance;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

            java.util.Date date1 = null;
            try {
                date1 = sdf.parse(str);
            } catch (ParseException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            Timestamp ts = new Timestamp(date1.getTime());
            r.setAttendance_time(ts);


            java.util.Date date11 = new java.util.Date();
            String strDate1 = new SimpleDateFormat("yyyy-MM-dd").format(date11);
            String leave = request.getParameter("leave_time");
            String str1 = strDate1 + " " + leave;

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");

            java.util.Date date111 = null;
            try {
                date111 = sdf1.parse(str1);
            } catch (ParseException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            Timestamp ts1 = new Timestamp(date111.getTime());
            r.setLeave_time(ts1);




            r.setTitle(request.getParameter("title"));
            r.setContent(request.getParameter("content"));

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            r.setCreated_at(currentTime);
            r.setUpdated_at(currentTime);

            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0) {
                em.close();

                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
                rd.forward(request, response);
            } else {
                em.getTransaction().begin();
                em.persist(r);
                em.getTransaction().commit();
                em.close();
                request.getSession().setAttribute("flush", "登録が完了しました。");

                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
        }
    }

}