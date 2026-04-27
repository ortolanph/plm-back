<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #2c3e50; border-bottom: 2px solid #3498db; }
        h2 { color: #34495e; margin-top: 20px; }
        table { border-collapse: collapse; width: 100%; margin: 10px 0; }
        th { background-color: #3498db; color: white; padding: 8px; text-align: left; }
        td { border: 1px solid #ddd; padding: 8px; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .lender-section { margin-bottom: 30px; border: 1px solid #ddd; padding: 15px; border-radius: 5px; }
        .info { color: #7f8c8d; }
    </style>
</head>
<body>
    <h1>Personal Load Manager Report</h1>
    <p class="info">Report Date: ${reportDate}</p>

    <#list lenders as lender>
    <div class="lender-section">
        <h2>${lender.name}</h2>
        <p class="info">
            <#if lender.phone?has_content>Phone: ${lender.phone}<br/></#if>
            <#if lender.bankData?has_content>Bank: ${lender.bankData}<br/></#if>
            <#if lender.address?has_content>Address: ${lender.address}</#if>
        </p>

        <h3>Transactions</h3>
        <#if lender.transactions?size &gt; 0>
        <table>
            <tr>
                <th>Date</th>
                <th>Value</th>
                <th>Type</th>
                <th>Payment Type</th>
            </tr>
            <#list lender.transactions as tx>
            <tr>
                <td>${tx.transactionDate}</td>
                <td>${tx.transactionValue?string(",##0.00")}</td>
                <td>${tx.transactionType}</td>
                <td><#if tx.transactionPaymentType?has_content>${tx.transactionPaymentType}</#if></td>
            </tr>
            </#list>
        </table>
        <#else>
        <p>No transactions found.</p>
        </#if>

        <h3>History</h3>
        <#if lender.history?size &gt; 0>
        <table>
            <tr>
                <th>Date</th>
                <th>Value</th>
                <th>Type</th>
                <th>History Type</th>
            </tr>
            <#list lender.history as h>
            <tr>
                <td>${h.historyDate}</td>
                <td><#if h.transactionValue?has_content>${h.transactionValue?string(",##0.00")}</#if></td>
                <td><#if h.transactionType?has_content>${h.transactionType}</#if></td>
                <td><#if h.historyType?has_content>${h.historyType}</#if></td>
            </tr>
            </#list>
        </table>
        <#else>
        <p>No history found.</p>
        </#if>
    </div>
    </#list>
</body>
</html>
