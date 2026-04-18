package app.action;


import app.dao.BaseDAO;
import app.framework.Cohort12Framework;
import app.framework.Cohort12Table;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.BigDecimalConverter;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BaseAction<T> extends HttpServlet {

    private BaseDAO<T> dao;

    protected BaseDAO<T> dao() {
        if (dao == null) {
            dao = new BaseDAO<>(this.getType());
        }
        return dao;
    }

    @SuppressWarnings("unchecked")
    public T serializeForm(Map<String, String[]> requestMap) {

        System.out.println("Form Serialization....");

        try {
            T clazzInstance = this.getType().newInstance();

            BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new ConvertUtilsBean() {
                @Override
                public Object convert(String value, Class clazz) {
                if (clazz.isEnum()) {
                    return Enum.valueOf(clazz, value);
                } else if (clazz == Date.class) {
                    // web forms return the date in the form
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        return dateFormat.parse(value);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return super.convert(value, clazz);
                }
                }
            });

            ConvertUtils.register(new BigDecimalConverter(), BigDecimal.class);

            beanUtilsBean.populate(clazzInstance, requestMap);

            return clazzInstance;

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e ) {
            throw new RuntimeException(e);
        }

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        T entity;
        try {
            entity = this.serializeForm(req.getParameterMap());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        dao().insert(entity);

        if (this.getType().isAnnotationPresent(Cohort12Table.class)) {
            resp.sendRedirect(this.getType()
                .getAnnotation(Cohort12Table.class).tableUrl());

        } else {
            resp.sendRedirect("./home");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException{

        //if session exist use it, otherwise create a new one
        HttpSession session = req.getSession();

        ServletConfig config = getServletConfig();

        PrintWriter writer = resp.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>");
        writer.println(config.getInitParameter("pageName"));
        writer.println("</title>");
        writer.println("<style>");
        writer.println("body { font-family: Arial; margin: 40px; background-color: #f4f6f8; }");
        writer.println("header { background-color: #2c3e50; color: white; padding: 15px; }");
        writer.println("section { margin-top: 20px; padding: 20px; background: white; border-radius: 5px; max-width: 400px; }");
        writer.println("input { width: 100%; padding: 8px; margin: 10px 0; }");
        writer.println("button { padding: 10px; background-color: #3498db; color: white; border: none; width: 100%; }");
        writer.println("a { display: inline-block; margin-top: 10px; color: #3498db; }");
        writer.println("</style>");
        writer.println("</head>");

        writer.println("<body>");

// Header
        writer.println("<header>");
        writer.println("<h1>");
        writer.println(config.getInitParameter("pageHeader"));
        writer.print("Logged In User: ");
        writer.println(session.getAttribute("UserActualName"));
        writer.println("</h1>");
        writer.println("</header>");

// Form
        writer.println("<section>");
        Cohort12Framework.htmlForm(writer, this.getType());
        writer.println("</section>");

        RequestDispatcher dispatcher = req.getRequestDispatcher("footer");
        dispatcher.include(req, resp);

        writer.println("</body>");
        writer.println("</html>");

    }

    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        ParameterizedType superClass =
            (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) superClass.getActualTypeArguments()[0];
    }

    public String dbName(){
        return this.getType().getSimpleName() + "_DB";
    }

    public List<T> returnData(){
        System.out.println("DB NAME: " + this.dbName());
        return dao().findAll();
    }

}
