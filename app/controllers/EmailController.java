
package controllers;

import models.*;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

public class EmailController extends Controller {

    private final MailerClient mailer;

    @Inject
    public EmailController(MailerClient mailer) {
        this.mailer = mailer;
    }

    public Result send(int saleId, int tranId) {
        List<LineItem> list = LineItem.findByTransactionId(tranId);
        Transaction t = Transaction.findById(tranId);
        Sale s = Sale.findById(saleId);
        String htmlReceipt = "";
        for (LineItem li : list) {
            htmlReceipt = htmlReceipt + "<tr>\n" +
                    "<th>" + li.getSaleItemId() + "</th>\n" +
                    "<th>" + li.getName() + "</th>\n" +
                    "<th>" + li.getDescription() + "</th>\n" +
                    "<th>" + li.getQuantity() + "</th>\n" +
                    "<th>$" + li.formatUnitPrice() + "</th>\n" +
                    "<th>$" + li.formatTotalPrice() + "</th>\n" +
                    "</tr>\n";
        }
        final Email email = new Email()
                .setSubject("Your Yard Sale Receipt")
                .setFrom("team18email@gmail.com")
                .addTo(t.buyerEmail)
                //.addAttachment("favicon.png", new File(Play.application().classloader().getResource("public/images/favicon.png").getPath(), cid))
                //.addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
                .setBodyText("A text message")
                .setBodyHtml("<html><body><div class=\"container content\">\n" +
                        "<p>Dear " + t.buyerName + "," +"</p>\n" +
                        "<p>Thank You for the following purchase on: " + t.formatDate() + " from</p>\n" +
                        "    <div class=\"row\">\n" +
                        "        <div class=\"col-sm-2\">\n" +
                        "            <h3 class=\"text-center\">" + s.name + "</h3>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-3\">\n" +
                        "            <h4 class=\"text-center\">Your payment method was: " + t.paymentMethod + "</h3>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-3\">\n" +
                        "            <h3 class=\"text-center\">Items Purchased: </h3>\n" +
                        "        </div>\n" +
                        "        <table class=\"table table-striped\">\n" +
                        "        <thead>\n" +
                        "        <tr>\n" +
                        "        <th>Catalog ID</th>\n" +
                        "        <th>Item</th>\n" +
                        "        <th>Description</th>\n" +
                        "        <th>Quantity</th>\n" +
                        "        <th>Unit Price</th>\n" +
                        "        <th>Total</th>\n" +
                        "        </tr>\n" +
                        "        </thead>\n" +
                        "        <tbody>\n" +
                        htmlReceipt +
                        "        </tbody>\n" +
                        "        </table>\n" +
                        "        <div class=\"col-sm-3\">\n" +
                        "            <h3  class=\"text-center\" >Total: $" + t.formatTotal() + "</h3>\n" +
                        "        </div>\n" +
                        "    </div>\n" + "</html>");
        String id = mailer.send(email);
        return redirect("/sale/" + saleId);
    }

    public Result sendDonor(int saleId) {
        User u = User.findByUsername(session("username"));
        Sale s = Sale.findById(saleId);
        int tranId = 0;
        float total = 0;
        String lines = "";

        List<LineItem> lis = LineItem.findBySaleIdUserCreatedId(s.id, u.id);
        for (LineItem li : lis) {
            total += li.getTotalPrice();
            lines +=
                "<tr>\n" +
                "<th>" + li.getTransactionId() + "</th>\n" +
                "<th>" + li.getName() + "</th>\n" +
                "<th>$" + li.formatUnitPrice() + "</th>\n" +
                "<th>" + li.getQuantity() + "</th>\n" +
                "<th>$" + li.formatTotalPrice() + "</th>\n" +
                "</tr>\n";
        }

        final Email email = new Email()
                .setSubject("Yard sale Donation Acknowledgement")
                .setFrom("team18email@gmail.com")
                .addTo(u.email)
                .setBodyText("Donor Acknowledgement Letter")
                .setBodyHtml("<html><link rel=\"stylesheet\" href=\"http://www.w3schools.com/lib/w3.css\">\n<body><div class=\"container content\">\n" +
                        "\n" +
                        "<div class=\"row\"><div class=\"col-sm-2\">\n" +
                        "\n " + u.firstName + " " + u.lastName + " \n<br>" + s.street + " \n<br>" +
                        s.city + ", " + s.state + " " + s.zip + "\n" +
                        "\n<br><p>" +
                        "Dear " + u.firstName + " " + u.lastName + ",\n" +
                        "<br><br>\n" +
                        "Thank you so much for your very generous donation.\n" +
                        "<br>\n" +
                        "<div class=\"col-sm-3\">\n" +
                        "<h3>Donated Items</h3>\n" +
                        "</div>\n" +
                        "<table class=\"table table-striped\">\n" +
                        "<thead>\n" +
                        "<tr>\n" +
                        "<th>Transaction</th>\n" +
                        "<th>Item</th>\n" +
                        "<th>Unit Price</th>\n" +
                        "<th>Quantity</th>\n" +
                        "<th>Total</th>\n" +
                        "</tr>\n" +
                        "</thead>\n" +
                        "<tbody>\n" +
                        lines +
                        "</tbody>\n" +
                        "</table>\n" +
                        "<div class=\"col-sm-3\">\n" +
                        "<h3>Total: $" + String.format("%.2f", total) + "</h3>\n" +
                        "</div>\n" +
                        "\n<br><br>" +
                        "Respectfully,\n" +
                        "\n<br>" +
                        " Yardsale Inc.\n" + "<br>" +
                        " Heaven's Highway,\n" + "<br>" +
                        " GA, 30281\n" + "<br>" +
                        " team18email@gmail.com\n" + "<br><p>" +
                        "\n<p>" +
                        "<font align = 'center'> DONATION RECEIPT – Keep for your records\n </font>" + "<br>" +
                        "\n" +
                        "Organization:  \t" + "Yardsale Inc." + "\n" + "<br>" +
                        "Cash Contribution:  \t$" + String.format("%.2f", total) + "<br>" +
                        "\n" +
                        "No goods or services were provided in exchange for your contribution.\n" + "<br>" +
                        "\n" +
                        "If you provided goods or services in exchange for the contribution, see IRS publication 1771 to determine if you are eligible.\n" +
                        "\n<br>" +
                        " \n<br>" +
                        "Note: The amount of the contribution that is deductible for federal income tax purposes is limited to the excess of money (and the fair market value of property other than money) contributed over the value of goods or services provided.\n</h3>\n" +
                        "    </div>\n" + "</html>");
        String id = mailer.send(email);
        return redirect("/sale/" + saleId);
    }
}
