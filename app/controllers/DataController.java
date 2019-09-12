package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import models.LineItem;
import models.Picture;
import models.Sale;
import models.SaleItem;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.loggedinNotFound;
import views.html.notFound;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static play.libs.Json.toJson;
/**
 * @author Nathan Cheek, Pablo Ortega, Hasan Qadri, Nick Yokley
 * This controller handles requests for data
 */
public class DataController extends Controller {

    /**
     * Gets an image stored in the database
     * @param id Id of image to return
     * @return Image with given Id
     */
    public Result getImage(int id) {
        Picture p = Picture.findById(id);
        if (p == null) {
            return notFound404();
        }
        InputStream is = new ByteArrayInputStream(p.image);
        return ok(is).as("image/jpeg");
    }

    /**
     * Lists all data about an item
     * @return JSON response of item data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getItem() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int id;
        try {
            id = Integer.parseInt(dynamicForm.get("id"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        SaleItem item  = SaleItem.findById(id);
        return ok(toJson(item));
    }

    /**
     * Lists all data about an item
     * @return JSON response of item data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getLineItem() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int id;
        try {
            id = Integer.parseInt(dynamicForm.get("id"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        LineItem item  = LineItem.findById(id);
        return ok(toJson(item));
    }

    /**
     * Lists all data about Line Items
     * @return JSON response of item data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getLineItems() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int tranId;
        try {
            tranId = Integer.parseInt(dynamicForm.get("tranId"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        List<LineItem> items  = LineItem.findByTransactionId(tranId);
        return ok(toJson(items));
    }

    /**
     * Lists all data about all items
     * @return JSON response of all item data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getItems() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int saleId;
        try {
            saleId = Integer.parseInt(dynamicForm.get("saleId"));
        } catch (NumberFormatException e) { // Null or non int string
            return notFound404();
        }
        List<SaleItem> items = SaleItem.findBySaleId(saleId);
        return ok(toJson(items));
    }

    /**
     * Get a QR Code image
     * @param content url the QR code should point to
     * @return QR Code
     */
    public Result getQrCode(String content) {
        content = "http://" + request().host() + content;
        QRCodeWriter qr = new QRCodeWriter();
        BitMatrix bm;
        try {
            bm = qr.encode(content, BarcodeFormat.QR_CODE, 150, 150);
        } catch (WriterException e) {
            return notFound404();
        }
        BufferedImage bi = MatrixToImageWriter.toBufferedImage(bm);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(bi, "png", os);
        } catch (IOException e) {
            return notFound404();
        }

        InputStream is = new ByteArrayInputStream(os.toByteArray());

        //ByteArrayInputStream output = null;
        return ok(is).as("image/png");
    }

    /**
     * Lists all data about all sales
     * @return JSON response of all sale data stored in database
     */
    @Security.Authenticated(Secured.class)
    public Result getSales() {
        List<Sale> sales = Sale.findAllOpen();
        return ok(toJson(sales));
    }

    /**
     * Returns Json array of items from a sale matching the query
     * @return items to json
     */
    @Security.Authenticated(Secured.class)
    public Result getSearchItems() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        if (dynamicForm.get("saleId") != null
                && dynamicForm.get("query") != null) {
            int saleId;
            try {
                saleId = Integer.parseInt(dynamicForm.get("saleId"));
            } catch (NumberFormatException e) { // Null or non int string
                return notFound404();
            }
            String query = "%" + dynamicForm.get("query") + "%";
            List<SaleItem> items =
                    Ebean.find(SaleItem.class).where().eq("saleId", saleId).or(
                    Expr.like("name", query),
                    Expr.like("description", query)
            ).findList();
            return ok(toJson(items));
        }
        return notFound404();
    }


    /**
     * Returns list of posts listed at the queried location
     * @return sales to json
     */
    @Security.Authenticated(Secured.class)
    public Result getSearchSales() {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String query = dynamicForm.get("query");
        if (query != null) {
            query = "%" + query + "%";
            List<Sale> sales = Ebean.find(Sale.class).where().or(
                Expr.like("name", query),
                Expr.or(
                    Expr.like("description", query),
                        Expr.or(
                            Expr.like("street", query),
                                Expr.or(
                                    Expr.like("city", query),
                                        Expr.or(
                                            Expr.like("state", query),
                                            Expr.like("zip", query)
                                        )
                                )
                        )
                )
            ).findList();
            return ok(toJson(sales));
        }
        return notFound404();
    }

    /**
     * Displays a 404 error page
     * @return HTTP response to a non existent page
     */
    public Result notFound404() {
        if (session("username") != null) {
            return notFound(loggedinNotFound.render());
        } else {
            return notFound(notFound.render());
        }
    }
}
